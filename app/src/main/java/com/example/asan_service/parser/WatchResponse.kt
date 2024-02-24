package com.example.asan_service.parser

data class WatchResponse(
    val status: Int,
    val message: String,
    val data: WatchData
)