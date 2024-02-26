package com.example.asan_service.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    var accXs : LiveData<List<Int>> = MutableLiveData(emptyList())
    var accYs : LiveData<List<Int>> = MutableLiveData(emptyList())
    var accZs : LiveData<List<Int>> = MutableLiveData(emptyList())
    var gyroXs : LiveData<List<Int>> = MutableLiveData(emptyList())
    var gyroYs : LiveData<List<Int>> = MutableLiveData(emptyList())
    var gyroZs : LiveData<List<Int>> = MutableLiveData(emptyList())
    var baros : LiveData<List<Int>> = MutableLiveData(emptyList())
    var lights : LiveData<List<Int>> = MutableLiveData(emptyList())
    var heartRates : LiveData<List<Int>> = MutableLiveData(emptyList())

    fun changeValue(newValue: String) {
        accXs = accXDao.getOldestData(newValue).map { it.map { it.value } }.asLiveData()
        accYs = accYDao.getOldestData(newValue).map { it.map { it.value } } .asLiveData()
        accZs = accZDao.getOldestData(newValue).map { it.map { it.value } } .asLiveData()
        gyroXs = gyroXDao.getOldestData(newValue).map { it.map { it.value } } .asLiveData()
        gyroYs = gyroYDao.getOldestData(newValue).map { it.map { it.value } } .asLiveData()
        gyroZs = gyroZDao.getOldestData(newValue).map { it.map { it.value } } .asLiveData()
        baros = baroDao.getOldestData(newValue).map { it.map { it.value } } .asLiveData()
        lights = lightDao.getOldestData(newValue).map { it.map { it.value } } .asLiveData()
        heartRates = heartRateDao.getOldestData(newValue).map { it.map { it.value } } .asLiveData()
    }
}