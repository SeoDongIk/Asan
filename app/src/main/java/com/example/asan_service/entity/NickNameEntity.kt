package com.example.asan_service.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class NickNameEntity(
    @PrimaryKey val watchId: String,
    val name : String
)