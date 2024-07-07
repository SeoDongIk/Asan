package com.example.asan_service

import android.app.*
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
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
import ua.naiksoftware.stomp.dto.StompHeader
import ua.naiksoftware.stomp.dto.StompMessage
import java.util.concurrent.TimeUnit

class MyWebSocketServiceForPosition : Service() {

    private val CHANNEL_ID = "MyForegroundServiceChannel2"
    private val NOTIFICATION_ID = 12345
    private lateinit var db: AppDatabase
    private lateinit var client : StompClient
    private val watchRepository by lazy { WatchRepository(db.watchItemDao(), StaticResource.apiServiceForSensor) }
    private val timers = mutableMapOf<String, Job>()

    companion object {
        var watchList: MutableList<WatchItem> = mutableListOf()
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        db = AppDatabase.getInstance(applicationContext)
        client = createStompClient()




    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("service3", "서비스 시작")
        val scope = CoroutineScope(Dispatchers.IO + Job())

        scope.launch {
            try {
                watchRepository.fetchWatchList()
                delay(3000)
                watchList = watchRepository.watchListLiveData.value!!.toMutableList()


                if (watchList != null) {
                    Log.d("service2", "0-1차 연결 : $watchList")
                    val watchIds: Set<String> = watchList.map { it.watchId.toString() }.toSet()

                    client.waitForConnection()
                    Log.d("service2", "0-2차 연결 : 소켓 연결 완료 (구독 준비 중)")
                    subscribeToSensorData(watchIds)
                }
            } catch (e: Exception) {
                Log.e("service2", "Error during onStartCommand", e)
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }




    private fun CoroutineScope.subscribeToSensorData(watchIds: Set<String>) {
        launch {
            Log.d("service3", "2차 연결 : 센서 데이터 구독 시작")
            val destinations = watchIds.map { "/queue/sensor/$it" }

            destinations.forEach { destination ->
                client.subscribe(destination) { message ->
                    val payload = JSONObject(message.payload)
                    Log.d("service3 for payload", payload.toString())

                    when (payload.getString("messageType")) {
                        "POSITION" -> handlePositionMessage(payload)
                        else -> Log.d("service3", "Unknown message type")
                    }
                }
            }
        }
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

        val client = Stomp.over(Stomp.ConnectionProvider.OKHTTP, StaticResource.getWsUrlForPosition(),null, okHttpClient)
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
            "Foreground Service Channel2",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private fun createNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("WebSocket Service2")
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