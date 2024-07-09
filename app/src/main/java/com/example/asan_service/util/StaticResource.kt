package com.example.asan_service.util

import com.example.asan_service.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object StaticResource {
    private const val ServerURL = "172.25.53.139"
//        private const val ServerURL = "192.168.37.213"
    private const val port = "8080"
    private const val portForPosition = "8083"

    val apiServiceForSensor: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(getHttpURL())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(ApiService::class.java)
    }

    val apiServiceForPosition: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(getHttpURLForPosition())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(ApiService::class.java)
    }

    fun getHttpURL(): String {
        return if (port.isEmpty()) "http://$ServerURL/" else "http://$ServerURL:$port/"
    }

    fun getHttpUrlForPosition(): String {
        return if (port.isEmpty()) "http://$ServerURL/" else "http://$ServerURL:$port/"
    }

    fun getHttpUrlForPositionWithoutSlash(): String {
        return if (port.isEmpty()) "http://$ServerURL/" else "http://$ServerURL:$portForPosition"
    }

    fun getWsURL(): String {
        return if (port.isEmpty()) "ws://$ServerURL/" else "ws://$ServerURL:$port/ws"
    }

    fun getWsUrlForPosition(): String {
        return if (port.isEmpty()) "ws://$ServerURL/" else "ws://$ServerURL:$portForPosition/ws"
    }

    fun getHttpURLForPosition(): String {
        return if (port.isEmpty()) "http://$ServerURL/" else "http://$ServerURL:$portForPosition/"
    }

    fun getHttpUrlWithoutSlash(): String {
        return if (port.isEmpty()) "http://$ServerURL" else "http://$ServerURL:$port"
    }


}