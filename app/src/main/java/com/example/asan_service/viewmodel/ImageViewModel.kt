package com.example.asan_service.viewmodel

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.asan_service.ApiService
import com.example.asan_service.CoordinateData
import com.example.asan_service.DeleteStateData
import com.example.asan_service.DragData
import com.example.asan_service.GetCoordinateResponse
import com.example.asan_service.GetImageResponse
import com.example.asan_service.ImageData
import com.example.asan_service.ImageDataList
import com.example.asan_service.ImageIdAndName
import com.example.asan_service.ImageListResponse
import com.example.asan_service.InsertStateData
import com.example.asan_service.NameHostData
import com.example.asan_service.PositionList
import com.example.asan_service.PositionListResponse
import com.example.asan_service.UploadImageResponse
import com.example.asan_service.dao.WatchItemDao
import com.example.asan_service.entity.WatchItemEntity
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream




class ImageViewModel() : ViewModel() {

    private val _imageData = MutableLiveData<ImageData?>()

    val imageData: LiveData<ImageData?> = _imageData

    private val _imageList = MutableLiveData<ImageDataList?>(null)
    val imageList: LiveData<ImageDataList?> = _imageList

    private val _coordinateList = MutableLiveData<List<CoordinateData>?>()
    val coordinateList: LiveData<List<CoordinateData>?> = _coordinateList

    private val _positionList = MutableLiveData<List<PositionList>?>()
    val positionList: LiveData<List<PositionList>?> = _positionList


    private val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.45.151:8080/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    private val apiService = retrofit.create(ApiService::class.java)

    // 이미지 목록 가져오기
    fun getImageList() {
        apiService.getImageList().enqueue(object : Callback<ImageListResponse> {
            override fun onResponse(call: Call<ImageListResponse>, response: Response<ImageListResponse>) {
                if (response.isSuccessful) {
                    // 이미지 목록 업데이트
                    _imageList.postValue(response.body()?.data)
                    Log.e("imagevalue",_imageList.value.toString())
                } else {
                    _imageList.postValue(null)
                }
            }

            override fun onFailure(call: Call<ImageListResponse>, t: Throwable) {
                _imageList.postValue(null)
            }
        })
    }



    fun getPositionList() {
        apiService.getPositionList().enqueue(object : Callback<PositionListResponse> {
            override fun onResponse(call: Call<PositionListResponse>, response: Response<PositionListResponse>) {
                if (response.isSuccessful) {
                    Log.d("responseList", response.body().toString())
                    // 성공적으로 데이터 리스트를 받았을 때 LiveData 업데이트
                    _positionList.postValue(response.body()?.data)

                } else {
                    // 오류 처리
                    Log.e("getPositionList", "Error fetching PositionList data: ${response.errorBody()?.string()}")
                    _positionList.postValue(null) // 오류가 발생하면 null로 설정
                }
            }

            override fun onFailure(call: Call<PositionListResponse>, t: Throwable) {
                // 통신 실패 처리
                Log.e("getPositionList", "Failed to fetch coordPositionListinate data", t)
                _positionList.postValue(null) // 통신 실패 시 null로 설정
            }
        })
    }


    fun ContentResolver.getFileName(uri: Uri): String {
        val cursor = query(uri, null, null, null, null)
        val nameIndex = cursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        cursor?.moveToFirst()
        val name = nameIndex?.let { cursor.getString(it) } ?: "tempFile"
        cursor?.close()
        return name
    }

    fun deleteImage(id: Long) {
        apiService.deleteImage(id).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    // Handle successful image deletion
                    Log.d("deleteImage", "Image successfully deleted")
                    getImageList() // Refresh the image list if needed
                } else {
                    // Handle error response
                    Log.e("deleteImage", "Error deleting image: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // Handle failure to communicate with the API
                Log.e("deleteImage", "Failed to delete image", t)
            }
        })
    }

    fun deleteCoordinate(PositionName: String) {
        apiService.deleteCoordinate(PositionName).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.d("deleteCoordinate", "Coordinate successfully deleted")
                    getImageList()
                } else {
                    Log.e("deleteCoordinate", "Error deleting Coordinate: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // Handle failure to communicate with the API
                Log.e("deleteImage", "Failed to delete Coordinate", t)
            }
        })
    }


    // 이미지 업로드
    fun uploadImage(context: Context, imageUri: Uri) {
        val contentResolver = context.contentResolver
        val parcelFileDescriptor = contentResolver.openFileDescriptor(imageUri, "r", null) ?: return
        val file = File(context.cacheDir, contentResolver.getFileName(imageUri))
        val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)

        val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("imageData", file.name, requestBody)

        val apiService = retrofit.create(ApiService::class.java)
        val call = apiService.uploadImage(part)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                response.body()?.let {
                    val gson = Gson()
                    try {
                        // 응답 본문을 UploadImageResponse 객체로 변환
                        val uploadResponse =
                            gson.fromJson(it.string(), UploadImageResponse::class.java)
                        // data 필드에서 imageId 추출
                        val imageId = uploadResponse.data

                        // imageId 사용
                        Log.d("Upload Success", "Image ID: $imageId")
                        getImageList()
                    } catch (e: JsonSyntaxException) {
                        Log.e("JSON Parsing Error", "Error parsing response", e)
                    }

                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // 통신 실패 시 처리
            }
        })
    }

    fun sendDragData(
        imageId: Long?,
        position: String,
        latitude : String,
        longitude : String,
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float
    ) {
        val apiService = retrofit.create(ApiService::class.java)

        val dragData = DragData(
            imageId = imageId,
            position = position,
            latitude = latitude,
            longitude = longitude,
            startX = startX,
            startY = startY,
            endX = endX,
            endY = endY
        )

        apiService.postImagePositionAndCoordinates(dragData).enqueue(object :
            Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.d("sendDragData", "Drag data successfully sent")
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("sendDragData", "Failed to send drag data")

            }
        })
    }



    fun insertState(androidId : String , imageId : Long,position : String){
        val state = InsertStateData(
            androidId = androidId,
            imageId = imageId,
            position = position
        )
        apiService.insertState(state).enqueue(object :
            Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.d("insertState", " State successfully insert")
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("sendDragData", "Failed to  insert State")

            }
        })
    }


    fun imageNameChange(id: Long, name : String ){
        val idNameData = ImageIdAndName(
            imageId = id,
            imageName = name
        )
        apiService.nameChange(idNameData).enqueue(object :
            Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.d("changeName", " name successfully change")
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("changeName", "Failed to change name")

            }
        })
    }

    fun changeName(id: Long, name : String , host : String){
        val nameHostData = NameHostData(
            name = name,
            host = host
        )
        apiService.changeName(id,nameHostData).enqueue(object :
            Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.d("changeName", " name successfully change")
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("changeName", "Failed to change name")

            }
        })
    }

    fun deleteState(androidId : String ){
        val state = DeleteStateData(
            androidId = androidId
        )
        apiService.deleteState(state).enqueue(object :
            Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.d("deleteState", " State successfully delete")
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("deleteState", "Failed to delete State")

            }
        })
    }





    fun fetchImageData(id: Long) {
        apiService.getImage(id).enqueue(object : Callback<GetImageResponse> {
            override fun onResponse(
                call: Call<GetImageResponse>,
                response: Response<GetImageResponse>
            ) {
                Log.e("nono", response.body()?.data.toString())
                if (response.isSuccessful) {
                    // 성공적으로 데이터를 받았을 때 MutableLiveData 업데이트
                    _imageData.postValue(response.body()?.data)

                } else {
                    // 오류 처리
                    Log.e("fetchImageData", "Error fetching image data: ${response.errorBody()?.string()}")
                    _imageData.postValue(null) // 필요에 따라 오류 상황을 나타내는 값을 설정
                }
            }

            override fun onFailure(call: Call<GetImageResponse>, t: Throwable) {
                // 통신 실패 처리
                Log.e("fetchImageData", "Failed to fetch image data", t)
                _imageData.postValue(null) // 필요에 따라 오류 상황을 나타내는 값을 설정
            }
        })
    }

    fun getPositionAndCoordinateList(id: Long) {
        apiService.getPositionAndCoordinateList(id).enqueue(object : Callback<GetCoordinateResponse> {
            override fun onResponse(
                call: Call<GetCoordinateResponse>,
                response: Response<GetCoordinateResponse>
            ) {
                Log.d("responseList", response.body().toString())
                if (response.isSuccessful) {
                    // 성공적으로 데이터 리스트를 받았을 때 LiveData 업데이트
                    _coordinateList.postValue(response.body()?.data)
                } else {
                    // 오류 처리
                    Log.e("getPositionAndCoordinateList", "Error fetching coordinate data: ${response.errorBody()?.string()}")
                    _coordinateList.postValue(null) // 오류가 발생하면 null로 설정
                }
            }

            override fun onFailure(call: Call<GetCoordinateResponse>, t: Throwable) {
                // 통신 실패 처리
                Log.e("getPositionAndCoordinateList", "Failed to fetch coordinate data", t)
                _coordinateList.postValue(null) // 통신 실패 시 null로 설정
            }
        })
    }
}