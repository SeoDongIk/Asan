package com.example.asan_service.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.example.asan_service.SendStateData
import com.example.asan_service.dao.*
import com.example.asan_service.data.User
import com.example.asan_service.util.StaticResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StaticalViewModel(
    private val userDao: WatchItemDao,
    private val accXDao: AccXDao,
    private val accYDao: AccYDao,
    private val accZDao: AccZDao,
    private val heartRateDao: HeartRateDao,
    ) : ViewModel() {
    private val apiService = StaticResource.apiServiceForSensor

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
    var accYs : LiveData<List<Pair<Long, Float>>> = MutableLiveData(emptyList())
    var accZs : LiveData<List<Pair<Long, Float>>> = MutableLiveData(emptyList())
    var heartRates : LiveData<List<Pair<Long, Float>>> = MutableLiveData(emptyList())

    fun changeValue(newValue: String) {
        viewModelScope.launch(Dispatchers.IO) {
            accXs = accXDao.getOldestData(newValue).map { it.map { Pair(it.timeStamp.toLong(), it.value) } }.asLiveData()
            accYs = accYDao.getOldestData(newValue).map { it.map { Pair(it.timeStamp.toLong(), it.value) } }.asLiveData()
            accZs = accZDao.getOldestData(newValue).map { it.map { Pair(it.timeStamp.toLong(), it.value) } }.asLiveData()
            heartRates = heartRateDao.getOldestData(newValue).map { it.map { Pair(it.timeStamp.toLong(), it.value.toFloat()) }}.asLiveData()
        }
    }

    fun insertSendState(id: Long){
        val sendStateData = SendStateData(
            watchId = id
        )

        apiService.insertSendState(sendStateData).enqueue(object :
            Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.d("insertSendState", " State successfully insert")


            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("insertSendState", "Failed to insert ")

            }
        })
    }


    fun deleteAccs() = viewModelScope.launch(Dispatchers.IO) {
        heartRateDao.deleteAllData()
        accXDao.deleteAllData()
        accYDao.deleteAllData()
        accZDao.deleteAllData()
    }
}