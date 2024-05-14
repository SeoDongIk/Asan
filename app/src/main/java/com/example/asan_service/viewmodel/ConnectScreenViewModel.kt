package com.example.asan_service.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.asan_service.dao.WatchItemDao
import com.example.asan_service.data.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ConnectScreenViewModel(private val userDao: WatchItemDao) : ViewModel() {

    val users: LiveData<List<User>> = userDao.getAll().map { watchItems ->
        watchItems.map { watchItem ->
            User(
                watchId = watchItem.watchId.toString(),
                name = watchItem.patientName,
                host = watchItem.patientRoom,
                connected = watchItem.isConnected,
                date = watchItem.measuredDate,
                modelName = watchItem.modelName
            )
        }

    }.asLiveData()

    val sortedUsers: LiveData<List<User>> = userDao.getAllSortedByWatchId().map { watchItems ->
        watchItems.map { watchItem ->
            User(
                watchId = watchItem.watchId.toString(),
                name = watchItem.patientName,
                host = watchItem.patientRoom,
                connected = watchItem.isConnected,
                date = watchItem.measuredDate,
                modelName = watchItem.modelName
            )
        }
    }


    fun changeNickName(watchId: String, newName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            userDao.updatePatientName(watchId, newName)
            // 업데이트 후 데이터 다시 불러오기
            val updatedUsers = userDao.getAll().first() // `first()`를 사용해 현재 데이터베이스 상태를 가져옴
            Log.d("ScannerSettingViewModel", "Updated users: ${updatedUsers.joinToString { user ->
                "WatchID: ${user.watchId}, Name: ${user.patientName}, con: ${user.isConnected}"
            }}")
        }

    }
}