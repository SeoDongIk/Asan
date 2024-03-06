package com.example.asan_service.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class PositionUpdateReceiver(private val onPositionReceived: (String) -> Unit) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val position = intent?.getStringExtra("position")
        position?.let {
            onPositionReceived(it)
        }
    }
}