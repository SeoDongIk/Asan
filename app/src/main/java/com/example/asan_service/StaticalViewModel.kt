package com.example.asan_service

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.asan_service.dao.*
import com.example.asan_service.data.User
import kotlinx.coroutines.flow.map

class StaticalViewModel(
    private val userDao: WatchItemDao,
    private val accXDao: AccXDao,
    private val accYDao: AccYDao,
    private val accZDao: AccZDao,
    private val gyroXDao: GyroXDao,
    private val gyroYDao: GyroYDao,
    private val gyroZDao: GyroZDao,
    private val lightDao: LightDao,
    private val heartRateDao: HeartRateDao,
    private val baroDao: BaroDao,
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

    val accXs : LiveData<List<Int>> = accXDao.getOldestData("2").map { it.map { it.value } }.asLiveData()
    val accYs : LiveData<List<Int>> = accYDao.getOldestData("2").map { it.map { it.value } } .asLiveData()
    val accZs : LiveData<List<Int>> = accZDao.getOldestData("2").map { it.map { it.value } } .asLiveData()
    val gyroXs : LiveData<List<Int>> = gyroXDao.getOldestData("2").map { it.map { it.value } } .asLiveData()
    val gyroYs : LiveData<List<Int>> = gyroYDao.getOldestData("2").map { it.map { it.value } } .asLiveData()
    val gyroZs : LiveData<List<Int>> = gyroZDao.getOldestData("2").map { it.map { it.value } } .asLiveData()
    val baros : LiveData<List<Int>> = baroDao.getOldestData("2").map { it.map { it.value } } .asLiveData()
    val lights : LiveData<List<Int>> = lightDao.getOldestData("2").map { it.map { it.value } } .asLiveData()
    val heartRates : LiveData<List<Int>> = heartRateDao.getOldestData("2").map { it.map { it.value } } .asLiveData()


}