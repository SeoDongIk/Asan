package com.example.asan_service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.navigation.NavController
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackgroundSettingScreen(navController : NavController) {
    val selectedImageUri = remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    var dragStart by remember { mutableStateOf(Offset.Zero) }
    var dragEnd by remember { mutableStateOf(Offset.Zero) }
    var isDragging by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        // 01 uri 스트링 불러오기
        val savedUriString = "dfdf"
        savedUriString?.let { uriString ->
            selectedImageUri.value = uriString.toUri()
        }
    }

    val getContent = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
        selectedImageUri.value = uri
        selectedImageUri.value?.let { uriString ->
            // 02 uri 스트링을 Room에 저장하기`
            if (uri != null) {
                uriToBitmap(context, uri)?.let { bitmap -> saveImage(context, bitmap) }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                title = {
                    Text(
                        "도면 설정"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate("MainScreen")
                    }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) {
        var expanded by remember { mutableStateOf(false) }
        var expanded_square by remember { mutableStateOf(false) }
        var setting_room : Int? by remember { mutableStateOf(null) }

        if (expanded) {
            AlertDialog(
                onDismissRequest = { expanded = false },
                title = { Text("주의 사항")},
                text = {
                    Text("도면 설정시 기존의 설정은 삭제됩니다.")
                },
                confirmButton = {
                    Button(onClick = { expanded = false }) {
                        Text("OK")
                    }
                }
            )
        }

        if (expanded_square) {
            AlertDialog(
                onDismissRequest = {
                    expanded_square = false
                    dragStart = Offset.Zero
                    dragEnd = Offset.Zero
                                   },
                title = { Text("도면에 설정할 호실을 선택해주세요", fontSize = 16.sp) },
                text = {
                    Column() {
                        // 호실을 불러오기.
                        // 호실과 setting_room을 함께 데이터베이스에 저장하기.
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        expanded_square = false
                        dragStart = Offset.Zero
                        dragEnd = Offset.Zero
                    }
                    ) {
                        Text("취소")
                    }
                }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(5f)
                        .fillMaxWidth()
                        .background(color = Color.Blue)
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { startOffset ->
                                    dragStart = startOffset
                                    isDragging = true
                                    Log.d("dfdf2", "y = ${startOffset.y.roundToInt()}")
                                },
                                onDrag = { change, dragAmount ->
                                    if (isDragging) {
                                        dragEnd = change.position
                                        Log.d("dfdf2", "y = ${change.position.y.roundToInt()} | \uD835\uDEE5 ${change.positionChange().y.roundToInt()}")
                                    }
                                },
                                onDragEnd = {
                                    saveDragRange(dragStart, dragEnd)
                                    Log.d("dfdf2", "end end!!!")
                                    expanded_square = true
                                }
                            )
                        }
                ) {
                    selectedImageUri.value?.let { uri ->
                        val bitmap = uriToBitmap(context, uri)
                        if (bitmap != null) {
                            Image(
                                bitmap = bitmap,
                                contentDescription = "Selected Image",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawIntoCanvas {
                            it.drawRect(
                                left = dragStart.x,
                                top = dragStart.y,
                                right = dragEnd.x,
                                bottom = dragEnd.y,
                                paint = Paint().apply { color = Color.Red }
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = {
                                getContent.launch("image/*")
                            },
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .weight(1f)
                        ) {
                            Text(text = "도면 설정")
                        }
                        Button(
                            onClick = {
                                expanded = true
                            },
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .weight(1f)
                        ) {
                            Text(text = "주의 사항")
                        }
                    }
                }
            }
        }
    }
}

fun uriToBitmap(context: Context, uri: Uri): ImageBitmap? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        bitmap.asImageBitmap()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun saveImage(context: Context, bitmap: ImageBitmap) {
    val file = File(context.filesDir, "saved_image.png")
    FileOutputStream(file).use {
        bitmap.asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 100, it)
    }
}

fun saveDragRange(start: Offset, end: Offset) {
    Log.d("dfdf2", start.toString() + " / " + end.toString()  )
}