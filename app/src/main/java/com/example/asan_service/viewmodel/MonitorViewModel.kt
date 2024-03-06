package com.example.asan_service.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.asan_service.ImageDataList
import com.example.asan_service.dao.WatchItemDao
import com.example.asan_service.entity.WatchItemEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MonitorViewModel() : ViewModel() {


    private val _watchPositions = MutableLiveData<MutableMap<String, String>?>()
    val watchPositions: MutableLiveData<MutableMap<String, String>?> = _watchPositions

    // 각 watchId에 대한 타이머를 관리하기 위한 맵
    private var timers = mutableMapOf<String, Job>()

    private val viewModelJob = SupervisorJob()
    private val viewModelScope = CoroutineScope(Dispatchers.Main + viewModelJob)



    fun handleStompMessage(watchId: String, position: String) {
        val updatedMap = _watchPositions.value.orEmpty().toMutableMap()



        // 위치 정보 업데이트
        updatedMap[watchId] = position
        Log.e("updatedMap",updatedMap.toString())
        _watchPositions.value = updatedMap
        Log.e("watchPositions",watchPositions.value.toString())



        // 이전에 설정된 타이머가 있으면 취소합니다.
        timers[watchId]?.cancel()

        // watchId에 대한 새 타이머를 설정합니다.
        timers[watchId] = viewModelScope.launch {
            delay(50000) // 5초 대기
            // 5초 후에 실행될 작업
            updatedMap.remove(watchId) // 해당 watchId 삭제
            _watchPositions.value = updatedMap // LiveData 업데이트
            timers.remove(watchId) // 타이머 맵에서 해당 타이머 제거

        }
        Log.e("watchPositions", watchPositions.value.toString())
    }

    override fun onCleared() {
        super.onCleared()
        // ViewModel이 정리될 때 모든 타이머를 취소합니다.
        timers.values.forEach { it.cancel() }
        viewModelJob.cancel() // ViewModel의 코루틴 작업도 취소
    }
}