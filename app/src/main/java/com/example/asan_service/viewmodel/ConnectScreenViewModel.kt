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
                date = watchItem.measuredDate,
                modelName = watchItem.modelName
            )
        }
    }.asLiveData()

    val connectedUsers: LiveData<List<User>> = userDao.getAll().map { watchItems ->
        // 먼저 watchItems를 필터링하여 connected가 true인 항목만 선택합니다.
        watchItems.filter { watchItem ->
            watchItem.isConnected
        }.map { watchItem ->
            // 필터링된 항목을 User 객체로 변환합니다.
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
}