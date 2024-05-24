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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Typeface
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.example.asan_service.util.PositionUpdateReceiver
import com.example.asan_service.viewmodel.ImageViewModel
import com.example.asan_service.viewmodel.MonitorViewModel
import com.example.asan_service.viewmodel.PasswordViewModel
import com.example.asan_service.viewmodel.WatchSettingViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class DotInfo(
    val dragosition: Offset,
//    val watchId: String,
    val name: String
)




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoniteringScreen(navController : NavController,viewModel: ImageViewModel, passwordViewModel : PasswordViewModel) {
    val context = LocalContext.current
    var dragStart by remember { mutableStateOf(Offset.Zero) }
    var dragEnd by remember { mutableStateOf(Offset.Zero) }
    var isDragging by remember { mutableStateOf(false) }
    val dragDataList = remember { mutableStateListOf<DragData>() }
    val imageId =
        navController.currentBackStackEntry?.arguments?.getString("imageId")?.toLongOrNull()
    val imageName = navController.currentBackStackEntry?.arguments?.getString("imageName")
    val imageData = viewModel.imageData.observeAsState().value
    val coordinateList = viewModel.coordinateList.observeAsState().value
    val dotInfos = remember { mutableStateListOf<DotInfo>() }
    val watchPositions = viewModel.watchPositions.observeAsState().value ?: emptyMap()
    val hasVisitedSettings by passwordViewModel.hasVisitedSettings.observeAsState()
    var secret_box by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf("") }




    LaunchedEffect(imageId) {
        imageId?.let {
            viewModel.fetchImageData(imageId)
            viewModel.getPositionAndCoordinateList(imageId)
        }

    }




    LaunchedEffect(watchPositions) {
        dotInfos.clear() // 기존 위치 정보를 클리어
        Log.e("업데이트 있니?", "네")
        Log.e("watchPositions3", watchPositions.toString())
        // dragDataList의 각 항목에 대해서 실행
        dragDataList.forEach { dragData ->
            // 현재 dragData의 position에 해당하는 watchId 및 name 정보를 담는 임시 리스트 생성
            val tempInfos = mutableListOf<DotInfo>()

            watchPositions?.forEach { (watchId, positionInfo) ->
                Log.e(
                    "positionInfoDebug",
                    "Comparing ${positionInfo.position} to ${dragData.position}"
                )
                if (positionInfo.position == dragData.position) {
                    val dotPosition = Offset(
                        dragData.startX + (dragData.endX - dragData.startX) / 2,
                        dragData.startY + (dragData.endY - dragData.startY) / 2
                    )
                    tempInfos.add(DotInfo(dotPosition, positionInfo.name))
                }
            }

            // 동일 position을 공유하는 모든 항목에 대하여 영역을 등분
            val count = tempInfos.size
            if (count > 0) {
                val areaWidth = dragData.endX - dragData.startX
                val segmentWidth = areaWidth / count

                tempInfos.forEachIndexed { index, info ->
                    val dotPositionX = dragData.startX + segmentWidth * index + segmentWidth / 2

                    val dotPositionY = (dragData.startY + dragData.endY) / 2
                    // 업데이트된 위치 정보로 DotInfo 객체를 다시 생성
                    dotInfos.add(
                        DotInfo(
                            Offset(dotPositionX, dotPositionY),
//                            info.watchId,
                            info.name
                        )
                    )
                }
                dotInfos.forEach { dotInfo ->
                    Log.e("DotInfo", "Name: ${dotInfo.name}, Position: ${dotInfo.dragosition}")
                }
            }
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
                }
            )
        }
    )

    {

        if (secret_box && !hasVisitedSettings!!) {

            AlertDialog(
                onDismissRequest = {
                    secret_box = false
                },
                title = {
                    Text("비밀번호를 입력해주세요.",
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                },
                text = {
                    BasicTextField(
                        value = text,
                        onValueChange = { newText ->
                            text = newText
                        },
                        modifier = Modifier
                            .border(
                                width = 1.dp,
                                color = Color(0x04, 0x61, 0x66),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .fillMaxWidth()
                            .padding(16.dp),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        )
                    )
                },
                confirmButton = {
                    Button(onClick = {
                        if(text == "1234") {
                            passwordViewModel.setHasVisitedSettings(true)
                            navController.navigate("BackgroundDetailScreen/$imageId?imageName=$imageName")
                        } else {
                            text = ""
                        }
                        secret_box = false
                    },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0x04, 0x61, 0x66)),
                    ) {
                        Text("입력")
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        text = ""
                        secret_box = false
                    },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0x04, 0x61, 0x66)),
                    ) {
                        Text("취소")
                    }
                }
            )
        }


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

                        Canvas(modifier = Modifier.fillMaxSize()) {

                            dotInfos.forEach { (position, name) ->
                                drawCircle(
                                    color = Color.Red,
                                    center = position,
                                    radius = 10f,
                                    style = Fill
                                )
                                Log.e("watchId1234",  " " +name)

                                drawContext.canvas.nativeCanvas.apply {
                                    save()
                                    rotate(90f, position.x, position.y)
                                    val textPaint = android.graphics.Paint().apply {
                                        color = android.graphics.Color.BLACK
                                        textSize = 35f
                                        textAlign = android.graphics.Paint.Align.CENTER
                                        typeface = android.graphics.Typeface.DEFAULT_BOLD
                                    }

                                    drawText(
                                        " Name: $name",
                                        position.x,
                                        position.y + 40f,
                                        textPaint
                                    )
                                    restore()
                                }


                            }
                        }
                    }
                }
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
                        if(!hasVisitedSettings!!) {
                            secret_box = true
                        }else{
                            navController.navigate("BackgroundDetailScreen/$imageId?imageName=$imageName")
                        }

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
