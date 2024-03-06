package com.example.asan_service

import android.app.*
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.room.Room
import com.example.asan_service.core.ApiService
import com.example.asan_service.core.AppDatabase
import com.example.asan_service.dao.AccXDao
import com.example.asan_service.dao.AccYDao
import com.example.asan_service.dao.AccZDao
import com.example.asan_service.dao.WatchItemDao
import com.example.asan_service.entity.*

import com.example.asan_service.viewmodel.MonitorViewModel
import kotlinx.coroutines.*
import org.json.JSONObject
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.StompHeader
import ua.naiksoftware.stomp.dto.StompMessage
import java.util.*

class MyWebSocketService : Service() {

    private val CHANNEL_ID = "MyForegroundServiceChannel"
    private val NOTIFICATION_ID = 12345
    private lateinit var db: AppDatabase

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        db = AppDatabase.getInstance(applicationContext)

        GlobalScope.launch(Dispatchers.IO) {
//            db.accXDao().deleteAllData()
//            db.accYDao().deleteAllData()
//            db.accZDao().deleteAllData()
//            db.heartRateDao().deleteAllData()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("service2", "서비스 시작")
        val watchService = ApiService.create()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = watchService.getWatchList("9999999")
                if (response.status == 200) {
                    val watchList = response.data.watchList
                    val watchIds = watchList.map { it.watchId }
                    val client = createStompClient()
                    Log.d("service2", "0-1차 연결 : " + response.toString())
                    GlobalScope.launch(Dispatchers.IO) {
                        client.waitForConnection()
                        Log.d("service2", "0-2차 연결 : " + "소켓 연결 완료 (구독 준비 중)")

                        val deferred = CompletableDeferred<Unit>()

                        val watchItemEntities = watchList.map {
                            WatchItemEntity(
                                watchId = it.watchId.toString(),
                                patientName = it.name,
                                patientRoom = it.host,
                                isConnected = false,
                                measuredDate = System.currentTimeMillis(),
                                modelName = "Abcdefghd"
                            )
                        }
                        db.watchItemDao().insertAll(watchItemEntities)

                        Log.d("service2", "1차 연결 : " + "연결된 워치 구독 시작")

                        client.subscribe("/queue/sensor/9999999") { message ->
                            Log.d("service2", "1차 연결 : " + "연결된 워치 구독 중")
                            val watchItemEntities = watchList.map {
                                WatchItemEntity(
                                    watchId = it.watchId.toString(),
                                    patientName = it.name,
                                    patientRoom = it.host,
                                    isConnected = it.watchId.toString() in JSONObject(message.payload).getString("data"),
                                    measuredDate = System.currentTimeMillis(),
                                    modelName = "Abcdefghd"
                                )
                            }
                            val connectedWatchItemEntities = watchItemEntities.filter { it.isConnected }
                            db.watchItemDao().insertAll(connectedWatchItemEntities)
                            Log.d("service2", "1차 연결 : " + "연결된 워치 구독 완료")
                            deferred.complete(Unit)
                        }

                        deferred.await()

                        Log.d("service2", "2차 연결 : " + "센서 데이터 구독 시작")

                        val destinationList = watchIds.toString().map {
                            "/queue/sensor/$it"
                        }

                        destinationList.forEach { destination ->
                            client.subscribe(destination) { message ->
                                Log.d("service2", message.payload)
                                when (JSONObject(message.payload).getString("messageType")) {
                                    "GYROSCOPE" -> {
                                        Log.d("service2", JSONObject(message.payload).getJSONObject("data").getDouble("gyroX").toString())
                                        Log.d("service2", "ok1")
                                        db.gyroXDao().insertData(GyroXEntity(watchId = destination.split("/").lastOrNull() ?: "0",value = JSONObject(message.payload).getJSONObject("data").getDouble("gyroX").toInt()))
                                        db.gyroYDao().insertData(GyroYEntity(watchId = destination.split("/").lastOrNull() ?: "0",value = JSONObject(message.payload).getJSONObject("data").getDouble("gyroY").toInt()))
                                        db.gyroZDao().insertData(GyroZEntity(watchId = destination.split("/").lastOrNull() ?: "0",value = JSONObject(message.payload).getJSONObject("data").getDouble("gyroZ").toInt()))

                                        JSONObject(message.payload).getJSONObject("data").getDouble("gyroX")
                                        JSONObject(message.payload).getJSONObject("data").getDouble("gyroY")
                                        JSONObject(message.payload).getJSONObject("data").getDouble("gyroZ")
                                    }
                                    "ACCELEROMETER" -> {
                                        Log.d("service2", JSONObject(message.payload).getJSONObject("data").getDouble("accX").toString())
                                        Log.d("service2", "ok2")
                                        db.accXDao().insertData(AccXEntity(watchId = destination.split("/").lastOrNull() ?: "0",value = (JSONObject(message.payload).getJSONObject("data").getDouble("accX")).toFloat(), timeStamp = (JSONObject(message.payload).getJSONObject("data").getString("timeStamp"))))
                                        db.accYDao().insertData(AccYEntity(watchId = destination.split("/").lastOrNull() ?: "0",value = (JSONObject(message.payload).getJSONObject("data").getDouble("accY")).toFloat(), timeStamp = (JSONObject(message.payload).getJSONObject("data").getString("timeStamp"))))
                                        db.accZDao().insertData(AccZEntity(watchId = destination.split("/").lastOrNull() ?: "0",value = (JSONObject(message.payload).getJSONObject("data").getDouble("accZ")).toFloat(), timeStamp = (JSONObject(message.payload).getJSONObject("data").getString("timeStamp"))))

                                        JSONObject(message.payload).getJSONObject("data").getDouble("accX")
                                        JSONObject(message.payload).getJSONObject("data").getDouble("accY")
                                        JSONObject(message.payload).getJSONObject("data").getDouble("accZ")
                                    }
                                    "LIGHT" -> {
                                        Log.d("service2", "ok3")
                                        JSONObject(message.payload).getJSONObject("data").getDouble("value")
                                    }
                                    "BAROMETER" -> {
                                        Log.d("service2", "ok4")
                                        JSONObject(message.payload).getJSONObject("data").getDouble("value")
                                    }
                                    "HEART_RATE" -> {
                                        Log.d("service2", "ok5")
                                        db.heartRateDao().insertData(HeartRateEntity(watchId = destination.split("/").lastOrNull() ?: "0",value = JSONObject(message.payload).getJSONObject("data").getInt("value"), timeStamp = (JSONObject(message.payload).getJSONObject("data").getString("timeStamp"))))

                                    }
                                    "POSITION" -> {
                                        val dataObject = JSONObject(message.payload).getJSONObject("data")
                                        val watchId = dataObject.optString("watchId")
                                        val position = dataObject.optString("position")

                                        Intent().also { intent ->
                                            intent.action = "com.example.asan_service.POSITION_UPDATE"
                                            intent.putExtra("watchId",watchId)
                                            intent.putExtra("position", position)
                                            LocalBroadcastManager.getInstance(this@MyWebSocketService).sendBroadcast(intent)
                                        }
                                    }
                                    else -> {
                                        Log.d("service2", "fail!!!")
                                    }
                                }
                            }
                        }

                    }

                } else {

                }
            } catch (e: Exception) {
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun createStompClient(): StompClient {
        val client = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://210.102.178.186:8080/ws")
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