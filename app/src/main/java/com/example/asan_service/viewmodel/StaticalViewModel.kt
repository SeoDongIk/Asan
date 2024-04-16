package com.example.asan_service.viewmodel

import androidx.lifecycle.*
import com.example.asan_service.dao.*
import com.example.asan_service.data.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class StaticalViewModel(
    private val userDao: WatchItemDao,
    private val accXDao: AccXDao,
    private val accYDao: AccYDao,
    private val accZDao: AccZDao,
    private val heartRateDao: HeartRateDao,
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
    var heartRates : LiveData<List<Pair<Long, Float>>> = MutableLiveData(emptyList())

    fun changeValue(newValue: String) {
        viewModelScope.launch(Dispatchers.IO) {
            accXs = accXDao.getOldestData(newValue).map { it.map { Pair(it.timeStamp.toLong(), it.value.toFloat()) } }.asLiveData()
            accYs = accYDao.getOldestData(newValue).map { it.map { Pair(it.timeStamp.toFloat(), it.value.toFloat()) } } .asLiveData()
            accZs = accZDao.getOldestData(newValue).map { it.map { Pair(it.timeStamp.toFloat(), it.value.toFloat()) } } .asLiveData()
            heartRates = heartRateDao.getOldestData(newValue).map { it.map { Pair(it.timeStamp.toLong(), it.value.toFloat()) }}.asLiveData()
        }
    }
}