package com.example.asan_service.feature

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.example.asan_service.DragData
import com.example.asan_service.ImageData

import android.graphics.BitmapFactory
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import android.util.Base64
import android.util.Log
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.example.asan_service.util.PositionUpdateReceiver
import com.example.asan_service.viewmodel.ImageViewModel
import com.example.asan_service.viewmodel.MonitorViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoniteringScreen(navController : NavController,viewModel: ImageViewModel) {
    val context = LocalContext.current
    var dragStart by remember { mutableStateOf(Offset.Zero) }
    var dragEnd by remember { mutableStateOf(Offset.Zero) }
    var isDragging by remember { mutableStateOf(false) }
    val dragDataList = remember { mutableStateListOf<DragData>() }
    val imageId = navController.currentBackStackEntry?.arguments?.getString("imageId")?.toLongOrNull()
    val imageName = navController.currentBackStackEntry?.arguments?.getString("imageName")
    val imageData = viewModel.imageData.observeAsState().value
    val coordinateList = viewModel.coordinateList.observeAsState().value
    val watchPositionsMap = remember { mutableStateOf<HashMap<String, String>>(HashMap()) }
    val coroutineScope = rememberCoroutineScope()
    val timers = remember { mutableMapOf<String, Job>() }



    DisposableEffect(context) {
        val intentFilter = IntentFilter("com.example.asan_service.POSITION_UPDATE")
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val watchId = intent?.getStringExtra("watchId")
                val position = intent?.getStringExtra("position")
                if (watchId != null && position != null) {
                    // 이전에 설정된 타이머가 있으면 취소합니다.
                    timers[watchId]?.cancel()

                    // Intent로 받은 값을 로컬 HashMap에 저장합니다.
                    val currentMap = HashMap(watchPositionsMap.value) // 새 HashMap 인스턴스 생성
                    currentMap[watchId] = position // 새 인스턴스에 값 추가
                    // 상태 업데이트를 트리거합니다.
                    watchPositionsMap.value = HashMap(currentMap)

                    // watchId에 대한 새 타이머를 설정합니다.
                    timers[watchId] = coroutineScope.launch {
                        delay(10000) // 10초 대기
                        // 10초 후에 실행될 작업: 해당 watchId 삭제
                        currentMap.remove(watchId)
                        watchPositionsMap.value = HashMap(currentMap) // 새 인스턴스로 상태 업데이트
                        // 타이머 맵에서 해당 타이머 제거
                        timers.remove(watchId)
                    }
                }
            }
        }
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, intentFilter)
        onDispose {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver)
            // 컴포저블이 제거될 때 모든 타이머를 취소합니다.
            timers.values.forEach { it.cancel() }
        }
    }


//


    // watchPositions가 변경될 때마다 로그를 출력하고, lastUpdateTimestamp를 업데이트합니다.
    LaunchedEffect(watchPositionsMap) {
        Log.d("MonitoringScreen", "watchPositions updated: ${watchPositionsMap}")
    }

    LaunchedEffect(imageId) {
        imageId?.let {
            viewModel.fetchImageData(imageId)
            viewModel.getPositionAndCoordinateList(imageId)
        }
    }

    LaunchedEffect(coordinateList) {
        coordinateList?.let { list ->
            dragDataList.clear() // 기존 리스트를 클리어
            list.forEach { coordinate ->
                // CoordinateData를 DragData로 변환하여 dragDataList에 추가
                dragDataList.add(
                    DragData(
                        imageId = coordinate.imageId,
                        position = coordinate.position,
                        latitude = coordinate.latitude,
                        longitude = coordinate.longitude,
                        startX = coordinate.startX,
                        startY = coordinate.startY,
                        endX = coordinate.endX,
                        endY = coordinate.endY
                    )
                )
            }
        }
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
                        text = if (imageId != null) {
                            "모니터링 : " + imageName
                        } else {
                            ""
                        },
                        color = Color.White
                    )},
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
                }
            )
        }
    )

    {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f)) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color = Color.White)
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDragStart = { startOffset ->
                                        dragStart = startOffset
                                        isDragging = true
                                    },
                                    onDrag = { change, dragAmount ->
                                        if (isDragging) {
                                            dragEnd = change.position
                                        }
                                    }
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        imageData?.let {
                            DisplayImageUrlImage(imageData.imageUrl)
                        }

//                        Canvas(modifier = Modifier.fillMaxSize()) {
//                            drawIntoCanvas { canvas ->
//                                dragDataList.forEach { dragData ->
//                                    Log.e("dragDatadragData", dragData.toString())
//                                    // 직사각형 그리기
//                                    canvas.drawRect(
//                                        left = dragData.startX,
//                                        top = dragData.startY,
//                                        right = dragData.endX,
//                                        bottom = dragData.endY,
//                                        paint = Paint().apply {
//                                            color = Color.Transparent
//                                        }
//                                    )
//                                    // 텍스트 그리기
//                                    val textPaint = android.graphics.Paint().apply {
//                                        color = android.graphics.Color.BLACK
//                                        textSize = 40f // 텍스트 크기 설정
//                                    }
//                                    val centerX = (dragData.startX + dragData.endX) / 2
//                                    val centerY = (dragData.startY + dragData.endY) / 2
//                                    canvas.nativeCanvas.drawText(
//                                        dragData.position,
//                                        centerX - textPaint.measureText(dragData.position) / 2, // 텍스트를 중앙에 위치시킵니다.
//                                        centerY + textPaint.textSize / 3, // 텍스트의 Y 위치를 조정합니다.
//                                        textPaint
//                                    )
//                                }
//                            }
//                            matchingPosition.value?.let { dragData ->
//                                drawCircle(
//                                    color = Color.Red,
//                                    center = Offset(
//                                        (dragData.startX + dragData.endX) / 2,
//                                        (dragData.startY + dragData.endY) / 2
//                                    ),
//                                    radius = 20f, // 크기를 조절하여 원하는 크기의 점을 그립니다.
//                                    style = Fill // 빨간 점을 채워서 그립니다.
//                                )
//                            }
//                        }
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            Log.e("watchPositionsMap",watchPositionsMap.toString())
                            // watchPositionsMap를 순회하며 각 position에 대한 빨간 점을 그립니다.
                            watchPositionsMap.value.forEach { (watchId, position) ->
                                // 현재 position에 해당하는 DragData를 찾습니다.
                                val dragData = dragDataList.find { it.position == position }
                                Log.e("씨발",dragData.toString())
                                dragData?.let {
                                    // 빨간 점의 위치를 계산합니다. 예시로, DragData의 중심 위치에 점을 그립니다.
                                    val dotPosition = Offset(
                                        x = (dragData.startX + dragData.endX) / 2,
                                        y = (dragData.startY + dragData.endY) / 2
                                    )

                                    drawCircle(
                                        color = Color.Red,
                                        center = dotPosition,
                                        radius = 20f, // 빨간 점의 크기. 필요에 따라 조절 가능
                                        style = Fill
                                    )
                                }
                            }
                        }

                    }
                }
            }


            Spacer(modifier = Modifier.weight(0.005f, fill = true))


            Spacer(modifier = Modifier.weight(0.005f, fill = true))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        navController.navigate("BackgroundDetailScreen/$imageId?imageName=$imageName")
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("도면 설정")
                }
            }
        }
    }
}


@Composable
fun DisplayImageUrlImage(imageUrl: String) {
    val context = LocalContext.current
    val imageBitmap = remember { mutableStateOf<ImageBitmap?>(null) }
    val fullUrl = "http://210.102.178.186:8080" + imageUrl
    Log.d("fullUrl",fullUrl)
    // 이미지 URL이 변경될 때마다 이미지를 다시 로드합니다.
    LaunchedEffect(fullUrl) {
        // 백그라운드 스레드에서 이미지 로딩
        withContext(Dispatchers.IO) {
            try {
                // Glide를 사용하여 이미지를 Bitmap으로 로드합니다.
                val bitmap = Glide.with(context)
                    .asBitmap()
                    .load(fullUrl)
                    .submit()
                    .get() // 동기 로딩

                // 로드된 Bitmap을 메인 스레드로 전달하여 ImageBitmap으로 변환
                withContext(Dispatchers.Main) {
                    imageBitmap.value = bitmap.asImageBitmap()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // 오류 처리: 필요한 경우 오류 로그를 출력하거나 사용자에게 메시지를 표시할 수 있습니다.
            }
        }
    }

    // 로드된 이미지가 있으면 표시합니다.
    imageBitmap.value?.let { bitmap ->
        Image(
            bitmap = bitmap,
            contentDescription = "Loaded Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
    }
}


fun calculateDotPosition(dragData: DragData, index: Int, total: Int): Offset {
    // 예시로, 각 위치에 대해 빨간 점을 수평으로 나열합니다.
    // 실제 구현에서는 dragData 범위와 index, total 값을 이용하여 적절한 위치 계산 로직을 추가해야 합니다.
    val gap = (dragData.endX - dragData.startX) / (total + 1) // 빨간 점 사이의 간격
    val x = dragData.startX + gap * (index + 1) // index에 따른 x 위치
    val y = (dragData.startY + dragData.endY) / 2 // y 위치는 범위의 중앙으로 고정

    return Offset(x, y)
}
