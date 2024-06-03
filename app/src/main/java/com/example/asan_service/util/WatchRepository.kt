package com.example.asan_service.util

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.asan_service.ApiService
import com.example.asan_service.dao.WatchItemDao
import com.example.asan_service.parser.WatchItem

class WatchRepository(private val userDao: WatchItemDao, private val apiService: ApiService) {

    private val _watchListLiveData = MutableLiveData<List<WatchItem>>()
    val watchListLiveData: LiveData<List<WatchItem>> = _watchListLiveData




     suspend fun fetchWatchList() {
        try {
            val response = apiService.getWatchList("9999999")
            if (response.status == 200) {
                val watchList = response.data.watchList
                Log.e("watchListwatchList", watchList.toString())
                _watchListLiveData.postValue(watchList)
            }
        } catch (e: Exception) {
            Log.e("WatchRepository", "Error fetching watch list", e)
        }
    }

    suspend fun deleteWatch(id: Long) {
        try {
            val response = apiService.deleteWatch(id).execute()
            if (response.isSuccessful) {
                userDao.deleteWatch(id.toString())
                fetchWatchList()
            }
        } catch (e: Exception) {
            Log.e("WatchRepository", "Error deleting watch", e)
        }
    }
}
