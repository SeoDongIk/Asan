package com.example.asan_service.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.asan_service.dao.*
import com.example.asan_service.data.User
import kotlinx.coroutines.flow.map

class StatisticScreenViewModel(
    private val userDao: WatchItemDao,
    private val accXDao: AccXDao,
    private val accYDao: AccYDao,
    private val accZDao: AccZDao,
    private val gyroXDao: GyroXDao,
    private val gyroYDao: GyroYDao,
    private val gyroZDao: GyroZDao,
    private val baroDao: BaroDao,
    private val lightDao: LightDao,
    private val dao: HeartRateDao
    ) : ViewModel() {
    val users: LiveData<List<User>> = userDao.getAll().map { watchItems ->
        watchItems.map { watchItem ->
            User(
                watchId = watchItem.watchId,
                name = watchItem.patientName,
                host = watchItem.patientRoom,
                connected = watchItem.isConnected,
                date = watchItem.measuredDate
            )
        }
    }.asLiveData()
}