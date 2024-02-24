package com.example.asan_service.core

import com.example.asan_service.dao.WatchItemDao
import com.example.asan_service.data.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRepository(private val userDao : WatchItemDao) {
    fun getAllUsers(): Flow<List<User>> = userDao.getAll().map { users ->
        users.map { userEntity ->
            User(
                watchId = userEntity.watchId,
                name = userEntity.patientName,
                host = userEntity.patientRoom,
                connected = userEntity.isConnected,
                date = userEntity.measuredDate
            )
        }
    }
}