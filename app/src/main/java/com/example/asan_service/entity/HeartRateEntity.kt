package com.example.asan_service.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class HeartRateEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val watchId : String,
    val value : Int
)