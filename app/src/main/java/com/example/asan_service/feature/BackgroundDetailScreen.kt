package com.example.asan_service

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.location.Location
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
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.asan_service.viewmodel.ImageViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackgroundDetailScreen(navController : NavController,viewModel: ImageViewModel) {
    val context = LocalContext.current
    var dragStart by remember { mutableStateOf(Offset.Zero) }
    var dragEnd by remember { mutableStateOf(Offset.Zero) }
    var isDragging by remember { mutableStateOf(false) }
    var textFieldValue by remember { mutableStateOf("") }
    var nameFieldValue by remember { mutableStateOf("") }
    val dragDataList = remember { mutableStateListOf<DragData>() }
    val imageId = navController.currentBackStackEntry?.arguments?.getString("imageId")?.toLong()
    val imageData = viewModel.imageData.observeAsState().value
    val coordinateList = viewModel.coordinateList.observeAsState().value
    var imageName by remember { mutableStateOf(navController.currentBackStackEntry?.arguments?.getString("imageName") ?: "") }

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
                            "도면 설정 : " + imageName
                        } else {
                            ""
                        },
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate("BackgroundSettingScreen")
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

        var expanded_square by remember { mutableStateOf(false) }
        var expanded_change_square by remember { mutableStateOf(false) }


        fun saveDragTextRange(start: Offset, end: Offset, text: String, latitude: String, longitude: String) {

            dragDataList.add(DragData(imageId, text,latitude,longitude, start.x, start.y, end.x, end.y))
            Log.d("checkpoint", DragData(imageId, text, latitude,longitude,start.x, start.y, end.x, end.y).toString())
            viewModel.sendDragData(imageId, text,latitude,longitude, start.x, start.y, end.x, end.y)
        }

        if (expanded_square) {
            AlertDialog(
                onDismissRequest = {
                    expanded_square = false
                    dragStart = Offset.Zero
                    dragEnd = Offset.Zero
                },
                title = { Text("도면에 설정할 호실을 입력해주세요", fontSize = 16.sp) },
                text = {

                    Column {
                        // 텍스트를 입력할 수 있는 상자를 만듭니다.
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .border(
                                    width = 1.dp,
                                    color = Color.Gray,
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .background(color = Color.White)
                        ) {
                            BasicTextField(
                                value = textFieldValue,
                                onValueChange = { textFieldValue = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        CoroutineScope(Dispatchers.Main).launch {
                            val location = getLocation(context)

                            Log.d("zxcv",location.toString())

                            val latitude = location?.latitude.toString()
                            val longitude = location?.longitude.toString()

                            val startX = dragStart.x
                            val endX = dragEnd.x
                            val startY = dragStart.y
                            val endY = dragEnd.y


                            saveDragTextRange(
                                Offset(startX, startY),
                                Offset(endX, endY),
                                textFieldValue,
                                latitude,
                                longitude
                            )

                            textFieldValue = ""
                            dragStart = Offset.Zero
                            dragEnd = Offset.Zero
                            expanded_square = false
                        }
                    }) {
                        Text("확인")
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        expanded_square = false
                        dragStart = Offset.Zero
                        dragEnd = Offset.Zero
                        textFieldValue = ""
                    }) {
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
                            .background(color = Color.Red.copy(alpha = 0.3f))
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
                                    },
                                    onDragEnd = {
                                        expanded_square = true
                                    }
                                )
                            }

                    ) {

                        imageData?.let {
                            DisplayImageUrlImage(imageData.imageUrl)
                        }

                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawIntoCanvas {
                                it.drawRect(
                                    left = dragStart.x,
                                    top = dragStart.y,
                                    right = dragEnd.x,
                                    bottom = dragEnd.y,
                                    paint = Paint().apply { color = Color.Red.copy(alpha = 0.3f) }
                                )
                            }
                        }

                        var showDeleteConfirmationDialog by remember { mutableStateOf(false) }
                        var selectedTextToDelete by remember { mutableStateOf<DragData?>(null) }
                        Canvas(modifier = Modifier.fillMaxSize().pointerInput(Unit) {
                            detectTapGestures { tapOffset ->
                                val tappedText = dragDataList.firstOrNull { dragData ->
                                    tapOffset.x >= dragData.startX && tapOffset.x <= dragData.endX &&
                                            tapOffset.y >= dragData.startY && tapOffset.y <= dragData.endY
                                }

                                if (tappedText != null) {
                                    selectedTextToDelete = tappedText
                                    showDeleteConfirmationDialog = true
                                }
                            }
                        }) {
                            drawIntoCanvas { canvas ->
                                dragDataList.forEach { dragData ->
                                    Log.e("dragDatadragData", dragData.toString())
                                    // 직사각형 그리기
                                    canvas.drawRect(
                                        left = dragData.startX,
                                        top = dragData.startY,
                                        right = dragData.endX,
                                        bottom = dragData.endY,
                                        paint = Paint().apply {
                                            color = Color.Red.copy(alpha = 0.3f)
                                        }
                                    )
                                    // 텍스트 그리기
                                    val textPaint = android.graphics.Paint().apply {
                                        color = android.graphics.Color.BLACK
                                        textSize = 40f // 텍스트 크기 설정
                                    }
                                    val centerX = (dragData.startX + dragData.endX) / 2
                                    val centerY = (dragData.startY + dragData.endY) / 2
                                    canvas.nativeCanvas.drawText(
                                        dragData.position,
                                        centerX - textPaint.measureText(dragData.position) / 2, // 텍스트를 중앙에 위치시킵니다.
                                        centerY + textPaint.textSize / 3, // 텍스트의 Y 위치를 조정합니다.
                                        textPaint
                                    )
                                }
                            }
                        }
                        if (showDeleteConfirmationDialog && selectedTextToDelete != null) {
                            AlertDialog(
                                onDismissRequest = { showDeleteConfirmationDialog = false },
                                title = { Text("해당 장소 삭제") },
                                text = { Text("해당 장소와 범위가 삭제됩니다: \"${selectedTextToDelete?.position}\"") },
                                confirmButton = {
                                    Button(onClick = {
                                        // Remove the selected text from the list
                                        viewModel.deleteCoordinate(selectedTextToDelete!!.position)
                                        selectedTextToDelete?.let { dragDataList.remove(it) }
                                        showDeleteConfirmationDialog = false
                                        selectedTextToDelete = null

                                    }) {
                                        Text("삭제")
                                    }
                                },
                                dismissButton = {
                                    Button(onClick = { showDeleteConfirmationDialog = false }) {
                                        Text("취소")
                                    }
                                }
                            )
                        }
                    }
                }
            }
            var showConfirmationDialog by remember { mutableStateOf(false) }

            Spacer(modifier = Modifier.weight(0.005f, fill = true))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        // Show confirmation dialog when the button is clicked
                        showConfirmationDialog = true
                    },
                    modifier = Modifier.padding(horizontal = 4.dp).weight(1f)
                ) {
                    Text("도면 삭제")
                }

                Button(
                    onClick = {
                        // Show confirmation dialog when the button is clicked
                        expanded_change_square = true
                    },
                    modifier = Modifier.padding(horizontal = 4.dp).weight(1f)
                ) {
                    Text("이름 변경")
                }
                if (expanded_change_square) {
                AlertDialog(
                    onDismissRequest = {
                        expanded_change_square = false
                    },
                    title = { Text("도면 이름을 입력해주세요", fontSize = 16.sp) },
                    text = {

                        Column {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                    .border(
                                        width = 1.dp,
                                        color = Color.Gray,
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    .background(color = Color.White)
                            ) {
                                BasicTextField(
                                    value = nameFieldValue,
                                    onValueChange = { nameFieldValue = it },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                )
                            }
                        }
                    },
                    confirmButton = {
                        Button(onClick = {
                            CoroutineScope(Dispatchers.Main).launch {
                                if (imageId != null) {
                                    viewModel.imageNameChange(imageId,nameFieldValue)
                                }
                                imageName = nameFieldValue
                                expanded_change_square = false
                            }
                        }) {
                            Text("확인")
                        }
                    },
                    dismissButton = {
                        Button(onClick = {
                            expanded_change_square = false
                            nameFieldValue = ""
                        }) {
                            Text("취소")
                        }
                    }
                )
            }

                Button(
                    onClick = { navController.navigate("MoniteringScreen/$imageId?imageName=$imageName") },
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .weight(1f)
                ) {
                    Text(text = "모니터링")
            } }



            // Confirmation dialog
            if (showConfirmationDialog) {
                AlertDialog(
                    onDismissRequest = {
                        // Dismiss the dialog without doing anything
                        showConfirmationDialog = false
                    },
                    title = { Text("삭제 확인") },
                    text = { Text(imageId.toString() + "번 도면을 삭제하시겠습니까?") },
                    confirmButton = {
                        Button(
                            onClick = {
                                // Perform deletion and navigate
                                if (imageId != null) {
                                    viewModel.deleteImage(imageId)
                                    navController.navigate("BackgroundSettingScreen")
                                }
                                showConfirmationDialog = false
                            }
                        ) {
                            Text("확인")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = {
                                // Dismiss the dialog
                                showConfirmationDialog = false
                            }
                        ) {
                            Text("취소")
                        }
                    }
                )
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



// Composable 함수 내에서 또는 Composable 함수가 아닌 곳에서 사용하기 위한 Context 파라미터 추가
suspend fun getLocation(context: Context): Location? {
    val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    // 권한 확인
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        // 위치 정보를 비동기적으로 가져옵니다.
        return try {
            fusedLocationClient.lastLocation.await()
        } catch (e: Exception) {
            null
        }
    } else {
        // 권한이 없는 경우, 권한 요청 필요
        return null
    }
}