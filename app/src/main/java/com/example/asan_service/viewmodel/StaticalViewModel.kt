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
                date = watchItem.measuredDate,
                modelName = watchItem.modelName
            )
        }
    }.asLiveData()

    var accXs : LiveData<List<Pair<Long, Float>>> = MutableLiveData(emptyList())
    var accYs : LiveData<List<Pair<Float, Float>>> = MutableLiveData(emptyList())
    var accZs : LiveData<List<Pair<Float, Float>>> = MutableLiveData(emptyList())
    var gyroXs : LiveData<List<Int>> = MutableLiveData(emptyList())
    var gyroYs : LiveData<List<Int>> = MutableLiveData(emptyList())
    var gyroZs : LiveData<List<Int>> = MutableLiveData(emptyList())
    var baros : LiveData<List<Int>> = MutableLiveData(emptyList())
    var lights : LiveData<List<Int>> = MutableLiveData(emptyList())
    var heartRates : LiveData<List<Pair<Long, Float>>> = MutableLiveData(emptyList())

    fun changeValue(newValue: String) {
        accXs = accXDao.getOldestData(newValue).map { it.map { Pair(it.timeStamp.toLong(), it.value.toFloat()) } }.asLiveData()
        accYs = accYDao.getOldestData(newValue).map { it.map { Pair(it.timeStamp.toFloat(), it.value.toFloat()) } } .asLiveData()
        accZs = accZDao.getOldestData(newValue).map { it.map { Pair(it.timeStamp.toFloat(), it.value.toFloat()) } } .asLiveData()
        gyroXs = gyroXDao.getOldestData(newValue).map { it.map { it.value } } .asLiveData()
        gyroYs = gyroYDao.getOldestData(newValue).map { it.map { it.value } } .asLiveData()
        gyroZs = gyroZDao.getOldestData(newValue).map { it.map { it.value } } .asLiveData()
        baros = baroDao.getOldestData(newValue).map { it.map { it.value } } .asLiveData()
        lights = lightDao.getOldestData(newValue).map { it.map { it.value } } .asLiveData()
        heartRates = heartRateDao.getOldestData(newValue).map { it.map { Pair(it.timeStamp.toLong(), it.value.toFloat()) }}.asLiveData()
    }
}