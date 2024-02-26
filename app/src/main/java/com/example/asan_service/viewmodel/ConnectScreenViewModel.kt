package com.example.asan_service.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.asan_service.dao.WatchItemDao
import com.example.asan_service.data.User
import kotlinx.coroutines.flow.map

class ConnectScreenViewModel(private val userDao: WatchItemDao) : ViewModel() {
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