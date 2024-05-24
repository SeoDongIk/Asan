package com.example.asan_service

import android.app.*
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.asan_service.core.ApiService
import com.example.asan_service.core.AppDatabase
import com.example.asan_service.entity.*
import com.example.asan_service.parser.WatchItem
import com.example.asan_service.util.PositionRepository
import com.example.asan_service.util.StaticResource
import com.example.asan_service.util.WatchRepository


import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import org.json.JSONObject
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompHeader
import ua.naiksoftware.stomp.dto.StompMessage
import java.util.concurrent.TimeUnit

class MyWebSocketService : Service() {

    private val CHANNEL_ID = "MyForegroundServiceChannel"
    private val NOTIFICATION_ID = 12345
    private lateinit var db: AppDatabase
    private lateinit var client : StompClient
    private val watchRepository by lazy { WatchRepository(db.watchItemDao(), StaticResource.apiService) }
    private val timers = mutableMapOf<String, Job>()

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        db = AppDatabase.getInstance(applicationContext)

        client = createStompClient()

        GlobalScope.launch(Dispatchers.IO) {
            db.accXDao().deleteAllData()
            db.accYDao().deleteAllData()
            db.accZDao().deleteAllData()
            db.heartRateDao().deleteAllData()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("service2", "서비스 시작")
        val watchService = ApiService.create()
        val scope = CoroutineScope(Dispatchers.IO + Job())

        scope.launch {
            try {
                watchRepository.fetchWatchList()
                delay(3000)
                var watchList = watchRepository.watchListLiveData.value?.toMutableList()

                if (watchList != null) {
                    Log.d("service2", "0-1차 연결 : $watchList")
                    val watchIds: Set<String> = watchList.map { it.watchId.toString() }.toSet()

                    client.waitForConnection()
                    Log.d("service2", "0-2차 연결 : 소켓 연결 완료 (구독 준비 중)")

                    subscribeToWatchListUpdates(watchList)
                    subscribeToSensorData(watchIds)
                }
            } catch (e: Exception) {
                Log.e("service2", "Error during onStartCommand", e)
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun CoroutineScope.subscribeToWatchListUpdates(watchList: MutableList<WatchItem>) {
        launch {
            client.subscribe("/queue/sensor/9999999") { message ->
                val payload = JSONObject(message.payload)
                Log.d("service2", "1차 연결 : 연결된 워치 구독 중 -> $payload")

                when (payload.getString("messageType")) {
                    "WATCH_LIST" -> handleWatchListMessage(payload.getString("data"), watchList)
                    "NEW_WATCH" -> handleNewListMessage(payload.getString("data"))
                    else -> Log.d("service2", "POSITION ok")
                }
            }
        }
    }

    private fun handleWatchListMessage(data: String, watchList: MutableList<WatchItem>) {
        Log.e("messageData", data)
        Log.e("watchList after deleting "  , watchList.toString())
        val inactiveWatchIds = watchList.filter { !data.contains(it.watchId.toString()) }
        Log.d("inactiveWatchIds", inactiveWatchIds.toString())

        val watchItemEntities = if (data == "[]") {
            watchList.map {
                WatchItemEntity(it.watchId.toString(), it.name, it.host, false, System.currentTimeMillis(), "Abcdefghd")
            }
        } else {
            val activeWatchItems = watchList.map {
                WatchItemEntity(it.watchId.toString(), it.name, it.host, true, System.currentTimeMillis(), "Abcdefghd")
            }
            val inactiveWatchItems = inactiveWatchIds.map {
                WatchItemEntity(it.watchId.toString(), it.name, it.host, false, System.currentTimeMillis(), "Abcdefghd")
            }
            activeWatchItems + inactiveWatchItems
        }

        Log.e("watchItemEntities", watchItemEntities.toString())
        db.watchItemDao().insertAll(watchItemEntities)
    }

    private fun handleNewListMessage(data: String) {
        Log.d("service2", "NEW_LIST message received with data: $data")
        val watchItemEntity = WatchItemEntity(
            watchId = data,
            patientName = "지정되지않음",
            patientRoom = "지정되지않음",
            isConnected = true,
            measuredDate = System.currentTimeMillis(),
            modelName = "Abcdefghd"
        )

        try {
            db.watchItemDao().insert(watchItemEntity)
            Log.d("service2", "New watch item inserted: $watchItemEntity")
        } catch (e: Exception) {
            Log.e("service2", "Error inserting new watch item", e)
        }
    }

    private fun CoroutineScope.subscribeToSensorData(watchIds: Set<String>) {
        launch {
            Log.d("service2", "2차 연결 : 센서 데이터 구독 시작")
            val destinations = watchIds.map { "/queue/sensor/$it" }

            destinations.forEach { destination ->
                client.subscribe(destination) { message ->
                    val payload = JSONObject(message.payload)
                    Log.d("service2", payload.toString())

                    when (payload.getString("messageType")) {
                        "ACCELEROMETER" -> handleAccelerometerMessage(payload, destination)
                        "HEART_RATE" -> handleHeartRateMessage(payload, destination)
                        "POSITION" -> handlePositionMessage(payload)
                        else -> Log.d("service2", "Unknown message type")
                    }
                }
            }
        }
    }

    private fun handleAccelerometerMessage(payload: JSONObject, destination: String) {
        val data = payload.getJSONObject("data")
        val watchId = destination.split("/").lastOrNull() ?: "0"
        val accX = data.getDouble("accX").toFloat()
        val accY = data.getDouble("accY").toFloat()
        val accZ = data.getDouble("accZ").toFloat()
        val timeStamp = data.getString("timeStamp")

        db.accXDao().insertData(AccXEntity(watchId = watchId,value = accX, timeStamp =  timeStamp))
        db.accYDao().insertData(AccYEntity(watchId = watchId,value = accY, timeStamp =  timeStamp))
        db.accZDao().insertData(AccZEntity(watchId = watchId,value = accZ, timeStamp =  timeStamp))
        Log.d("service2", "ACCELEROMETER ok")
    }

    private fun handleHeartRateMessage(payload: JSONObject, destination: String) {
        val data = payload.getJSONObject("data")
        val watchId = destination.split("/").lastOrNull() ?: "0"
        val value = data.getInt("value")
        val timeStamp = data.getString("timeStamp")

        db.heartRateDao().insertData(HeartRateEntity(watchId = watchId, value = value, timeStamp = timeStamp))
        Log.d("service2", "HEART_RATE ok")
    }

    private fun handlePositionMessage(payload: JSONObject) {
        val dataObject = payload.getJSONObject("data")
        val watchId = dataObject.optString("watchId")
        val position = dataObject.optString("position")
        val name = dataObject.optString("watchName")
        if(position.isNotEmpty() && !position.equals("null")){
            PositionRepository.updatePosition(watchId, position, name)
        }

        timers[watchId]?.cancel()

        val job = CoroutineScope(Dispatchers.IO).launch {
            delay(20000)
            PositionRepository.removePosition(watchId)
            timers.remove(watchId)
        }

        timers[watchId] = job
    }


    private fun createStompClient(): StompClient {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(180, TimeUnit.SECONDS) // 30초로 연결 시간 초과 설정
            .build()

        val client = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://210.102.178.186:8080/ws",null, okHttpClient)
        val headers = arrayListOf<StompHeader>()

        headers.add(StompHeader("Authorization", "9999999"))
        headers.add(StompHeader("watchId", "9999999"))
        client.connect(headers)

        return client
    }

    private suspend fun StompClient.waitForConnection() {
        while (!isConnected) {
            delay(100)
        }
    }

    private suspend fun StompClient.subscribe(destination: String, callback: (StompMessage) -> Unit) {
        withContext(Dispatchers.IO) {
            this@subscribe.topic(destination).subscribe(callback)
        }
    }

    private suspend fun StompClient.disconnect() {
        withContext(Dispatchers.IO) {
            this@disconnect.disconnect()
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Foreground Service Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private fun createNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("WebSocket Service")
            .setContentText("WebSocket service is running...")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}