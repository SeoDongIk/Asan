package com.example.asan_service.viewmodel

import android.util.Log
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.asan_service.ApiService
import com.example.asan_service.BeaconCount
import com.example.asan_service.BeaconCountResponse
import com.example.asan_service.ImageDataList
import com.example.asan_service.ImageListResponse
import com.example.asan_service.NameHostData
import com.example.asan_service.PositionList
import com.example.asan_service.PositionNameData
import com.example.asan_service.SendStateData
import com.example.asan_service.dao.WatchItemDao
import com.example.asan_service.parser.WatchItem
import com.example.asan_service.util.StaticResource
import com.example.asan_service.util.WatchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WatchSettingViewModel(private val repository: WatchRepository, private val userDao: WatchItemDao) : ViewModel() {

    private val _beaconCountList = MutableLiveData<List<BeaconCount>?>()
    val beaconCountList: LiveData<List<BeaconCount>?> = _beaconCountList


    private val apiService = StaticResource.apiServiceForSensor
    private val apiServiceForPosition = StaticResource.apiServiceForPosition


    fun deleteWatch(id: Long) {
        apiService.deleteWatch(id).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    // Handle successful image deletion
                    viewModelScope.launch(Dispatchers.IO) {
                        userDao.deleteWatch(id.toString())
                        repository.fetchWatchList()
                    }

                    Log.d("deleteWatch", "watch successfully deleted")
                    //getWatchList() // Refresh the image list if needed
                } else {
                    // Handle error response
                    Log.e("deleteWatch", "Error deleting watch: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // Handle failure to communicate with the API
                Log.e("deleteWatch", "Failed to delete watch", t)
            }
        })
    }

    fun getCountBeacon() {
        apiServiceForPosition.countBeacon().enqueue(object : Callback<BeaconCountResponse> {
            override fun onResponse(call: Call<BeaconCountResponse>, response: Response<BeaconCountResponse>) {
                if (response.isSuccessful) {
                    // 이미지 목록 업데이트
                    _beaconCountList.postValue(response.body()?.data)
                    Log.d("_beaconCountList",response.body()?.data.toString())

                } else {
                    _beaconCountList.postValue(null)
                }
            }

            override fun onFailure(call: Call<BeaconCountResponse>, t: Throwable) {
                Log.e("cant Count Beacon","Fail to count beacon")
            }
        })
    }

    fun deleteBeacon(positionNameData: PositionNameData) {
        apiServiceForPosition.deleteBeacon(positionNameData).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    // Handle successful image deletion
                    Log.d("deleteBeacon", "Beacon successfully deleted")
                    getCountBeacon()
                } else {
                    // Handle error response
                    Log.e("deleteBeacon", "Error deleting beacon: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // Handle failure to communicate with the API
                Log.e("deleteBeacon", "Failed to delete beacon", t)
            }
        })
    }

}


