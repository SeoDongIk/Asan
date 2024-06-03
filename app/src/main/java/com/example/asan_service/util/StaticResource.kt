package com.example.asan_service.util

import com.example.asan_service.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object StaticResource {
//    private const val ServerURL = "210.102.178.186"
    private const val ServerURL = "192.168.37.213"
    private const val port = "8080"

    val apiService: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(getHttpURL())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(ApiService::class.java)
    }

    fun getHttpURL(): String {
        return if (port.isEmpty()) "http://$ServerURL/" else "http://$ServerURL:$port/"
    }

    fun getHttpUrlWithoutSlash(): String {
        return if (port.isEmpty()) "http://$ServerURL" else "http://$ServerURL:$port"
    }

    fun getWsURL(): String {
        return if (port.isEmpty()) "ws://$ServerURL/" else "ws://$ServerURL:$port/ws"
    }
}