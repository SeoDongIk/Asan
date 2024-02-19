package com.example.asan_service.core

import com.example.asan_service.data.WatchResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ApiService {
    @GET("/api/watch")
    suspend fun getWatchList(): WatchResponse

    companion object {
        private const val BASE_URL = "http://210.102.178.186"

        fun create(): ApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}