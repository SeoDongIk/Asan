package com.example.asan_service



import com.example.asan_service.parser.WatchResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path


data class ImageData(
    val imageId: Long,
    val imageUrl: String
)


data class PositionState(
    val id : Long,
    val imageId : Long,
    val position : String,
    val startTime : Long,
    val endTime: Long
)

data class CoordinateData(
    val imageId: Long,
    val coordinateId: Long,
    val latitude: String,
    val longitude : String,
    val position: String,
    val startX: Float,
    val startY: Float,
    val endX: Float,
    val endY: Float
)

data class ImageDataList(
    val imageIds: List<Long>,
    val imageNames : List<String>
)

data class ImageIdAndName(
    val imageId: Long,
    val imageName : String
)

data class PositionList(
    val imageId: Long,
    val coordinateId : Long,
    val position : String
)

data class GetCoordinateResponse(
    val status: Int,
    val message: String,
    val data: List<CoordinateData>
)

data class GetImageResponse(
    val status: Int,
    val message: String,
    val data: ImageData
)

data class BeaconCount(
    val position : String,
    val counts : Int
)

data class UploadImageResponse(
    val status: Int,
    val message: String,
    val data: Long
)

data class ImageListResponse(
    val status: Int,
    val message: String,
    val data: ImageDataList
)



data class StatusResponse(
    val status: Int,
    val message: String,
    val data: PositionState
)

data class PositionListResponse(
    val status: Int,
    val message: String,
    val data: List<PositionList>
)


data class BeaconCountResponse(
    val status: Int,
    val message: String,
    val data: List<BeaconCount>
)

data class InsertStateData(
    val androidId: String,
    val imageId: Long,
    val position: String,
    val endTime : Long
)

data class DeleteStateData(
    val androidId: String
)

data class NameHostData(
    val name: String,
    val host: String,
)

data class PositionNameData(
    val position : String
)




interface ApiService {

    @GET("/api/watch")
    suspend fun getWatchList(@Header("Authorization") token: String): WatchResponse

    @Multipart
    @POST("api/image/saveImage")
    fun uploadImage(@Part image: MultipartBody.Part): Call<ResponseBody>

    @POST("api/image/postImagePositionAndCoordinates")
    fun postImagePositionAndCoordinates(@Body dragData: DragData): Call<ResponseBody>


    // 이미지 이름 변경
    @POST("/api/image/nameChange")
    fun nameChange(@Body state: ImageIdAndName): Call<ResponseBody>

    @POST("/api/location/insertState")
    fun insertState(@Body state: InsertStateData): Call<ResponseBody>

    @POST("/api/location/deleteState")
    fun deleteState(@Body state: DeleteStateData): Call<ResponseBody>

    // 환자 이름 변경
    @POST("/api/watch/{id}")
    fun changeName(@Path("id") id: Long, @Body namehostData: NameHostData ): Call<ResponseBody>


    @GET("/api/location/getCollectionStatus/{id}")
    fun getCollectionStatus(@Path("id") androidId : String) : Call<StatusResponse>


    @GET("api/image/getImage/{id}")
    fun getImage(@Path("id") id: Long): Call<GetImageResponse>

    @GET("api/location/countBeacon")
    fun countBeacon(): Call<BeaconCountResponse>

    @GET("api/image/getPositionAndCoordinateList/{id}")
    fun getPositionAndCoordinateList(@Path("id") id: Long): Call<GetCoordinateResponse>

    @GET("api/image/getImageList")
    fun getImageList(): Call<ImageListResponse>

    @GET("api/image/getPositionList")
    fun getPositionList(): Call<PositionListResponse>

    @DELETE("api/image/deleteImage/{id}")
    fun deleteImage(@Path("id") id: Long): Call<ResponseBody>

    @DELETE("api/watch/delete/{id}")
    fun deleteWatch(@Path("id") id: Long): Call<ResponseBody>


    @HTTP(method = "DELETE", path = "api/location/deleteBeacon", hasBody = true)
    fun deleteBeacon(@Body positionNameData: PositionNameData): Call<ResponseBody>


    @DELETE("api/image/deleteImagePositionAndCoordinates/{positionName}")
    fun deleteCoordinate(@Path("positionName") positionName: String): Call<ResponseBody>
}