package com.example.asan_service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.io.File
import java.io.FileOutputStream

import android.util.Base64
import android.util.Log


import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.ViewModel
import com.example.asan_service.viewmodel.ImageViewModel
import com.example.asan_service.viewmodel.PasswordViewModel
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import java.io.ByteArrayOutputStream
import java.io.FileInputStream

data class DragData(
    val imageId: Long?,
    val position: String,
    val latitude: String,
    val longitude : String,
    val startX: Float,
    val startY: Float,
    val endX: Float,
    val endY: Float
)





@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackgroundSettingScreen(navController : NavController,viewModel: ImageViewModel,passwordViewModel: PasswordViewModel) {
    val selectedImageUri = remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val imageList by viewModel.imageList.observeAsState()


    val getContent =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            selectedImageUri.value = uri
            selectedImageUri.value?.let { uriString ->
                viewModel.uploadImage(context, uriString)}
        }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0x04, 0x61, 0x66),
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                title = {
                    Text(
                        "도면 설정",
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate("MainScreen")
                    }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { getContent.launch("image/*") }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "도면 추가",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    )
    {
        ImageSettingListDisplay(imageList=imageList,navController=navController )
    }
}


@Composable
fun ImageSettingListDisplay(imageList: ImageDataList?, navController: NavController) {
    LazyColumn {
        imageList?.imageIds?.forEachIndexed { index, imageId ->
            item {
                Spacer(modifier = Modifier.size(4.dp))
                Column(
                    modifier = Modifier.padding(horizontal = 4.dp)
                        .border(BorderStroke(1.dp, Color(0x04, 0x61, 0x66)), shape = RoundedCornerShape(8.dp))
                        .padding(4.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = "도면 id : $imageId",
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        val imageName = imageList.imageNames.getOrNull(index) ?: "Unknown"
                        Text(
                            text = "도면 이름 : $imageName",
                            textAlign = TextAlign.Center
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        val imageName = imageList.imageNames.getOrNull(index) ?: "Unknown"
                        Button(
                            onClick = { navController.navigate("BackgroundDetailScreen/$imageId?imageName=$imageName") },
                            modifier = Modifier.padding(4.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0x04, 0x61, 0x66)),
                        ) {
                            Text(text = "세부 설정")
                        }
                    }
                }
            }
        }
    }
}

fun saveDragRange(start: Offset, end: Offset) {
}

enum class Screen {
    First,
    Second
}