package com.example.asan_service

import android.app.*
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.asan_service.core.ApiService
import kotlinx.coroutines.*
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.StompHeader
import ua.naiksoftware.stomp.dto.StompMessage

class MyWebSocketService : Service() {

    private val CHANNEL_ID = "MyForegroundServiceChannel"
    private val NOTIFICATION_ID = 12345

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val watchService = ApiService.create()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = watchService.getWatchList("9999999")
                if (response.status == 200) {
                    val watchList = response.data.watchList
//                    Log.d("dfdf", watchList.toString())
                } else {

                }
            } catch (e: Exception) {

            }
        }

        val client = createStompClient()
        GlobalScope.launch(Dispatchers.IO) {
            client.waitForConnection()
            Log.d("dfdf", "끝")
            client.subscribe("/queue/sensor/9999999") {
                Log.d("dfdf", it.toString())
            }
            client.subscribe("/queue/sensor/2") {
                Log.d("dfdf", it.toString())
            }
            client.subscribe("/queue/sensor/3") {
                Log.d("dfdf", it.toString())
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

        client.lifecycle().subscribe { state ->
            Log.d("dfdf", state.type.toString())
        }

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