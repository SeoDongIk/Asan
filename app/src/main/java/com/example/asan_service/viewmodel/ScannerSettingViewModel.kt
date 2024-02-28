package com.example.asan_service.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.asan_service.dao.*
import com.example.asan_service.data.NickName
import com.example.asan_service.data.User
import com.example.asan_service.entity.NickNameEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ScannerSettingViewModel(
    private val userDao: WatchItemDao,
    private val nickNameDao: NickNameDao
) : ViewModel() {

    val viewModelScope = CoroutineScope(Dispatchers.IO)

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

    val nickName : LiveData<List<NickName>> = nickNameDao.getAll().map { nickNames ->
        nickNames.map { nickname ->
            NickName(
                watchId = nickname.watchId,
                name = nickname.name
            )
        }
    }.asLiveData()

    fun changeNickName(watchId : String, newName : String) {

        viewModelScope.launch {
            nickNameDao.insert(NickNameEntity(watchId = watchId, name = newName))
        }
    }

}