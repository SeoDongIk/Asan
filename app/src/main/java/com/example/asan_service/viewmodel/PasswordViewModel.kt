package com.example.asan_service.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PasswordViewModel : ViewModel() {

    private val _hasVisitedSettings = MutableLiveData(false)
    val hasVisitedSettings: LiveData<Boolean> = _hasVisitedSettings




    // hasVisitedSettings 값을 업데이트하는 메소드
    fun setHasVisitedSettings(value: Boolean) {
        _hasVisitedSettings.value = value
    }
}