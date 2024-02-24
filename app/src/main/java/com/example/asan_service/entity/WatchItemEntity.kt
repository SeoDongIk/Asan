package com.example.asan_service.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watch_item_entities")
data class WatchItemEntity(
    @PrimaryKey val watchId: String,
    val patientName: String,
    val patientRoom: String,
    val isConnected: Boolean,
    val measuredDate: String
)