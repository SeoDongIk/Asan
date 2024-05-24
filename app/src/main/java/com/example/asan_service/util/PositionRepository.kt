package com.example.asan_service.util

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object PositionRepository {
    private val _positions = MutableLiveData<Map<String, PositionInfo>>()
    val positions: LiveData<Map<String, PositionInfo>> get() = _positions


    fun updatePosition(watchId: String, position: String, name: String) {
        val currentPositions = _positions.value.orEmpty().toMutableMap()
        currentPositions[watchId] = PositionInfo(position, name)
        _positions.postValue(currentPositions)
    }

    fun removePosition(watchId: String) {
        val currentPositions = _positions.value.orEmpty().toMutableMap()
        currentPositions.remove(watchId)
        _positions.postValue(currentPositions)
    }



}

data class PositionInfo(val position: String, val name: String, val timestamp: Long = System.currentTimeMillis())
