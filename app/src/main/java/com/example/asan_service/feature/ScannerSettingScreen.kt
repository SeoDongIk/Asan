package com.example.asan_service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavController
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToInt

import org.json.JSONObject
import android.util.Base64
import androidx.compose.runtime.livedata.observeAsState
import com.example.asan_service.viewmodel.ConnectScreenViewModel
import java.io.ByteArrayOutputStream



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerSettingScreen (navController : NavController,viewModel: ConnectScreenViewModel) {
    val usersState by viewModel.connectedUsers.observeAsState(initial = emptyList())


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0x04, 0x61, 0x66),
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                title = {
                    Text(
                        "설정",
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


        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
        ) {
            if (usersState.isEmpty()) {
                item {
                    Text(
                        "연결된 워치가 없습니다. 연결 상태를 확인해주세요.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                items(usersState) { userState -> // `userState`는 리스트의 각 항목을 나타냅니다.
                    Column(
                        modifier = Modifier
                            .border(
                                BorderStroke(1.dp, Color(0x04, 0x61, 0x66)),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .fillMaxWidth()
                            .padding(4.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Text(text = "watch id: ${userState.watchId}")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "환자 이름: ${userState.name}")
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = { navController.navigate("WatchSettingScreen/${userState.watchId}") }, // `usersState`를 `userState`로 변경
                                modifier = Modifier.padding(4.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0x04, 0x61, 0x66)
                                ),
                            ) {
                                Text(text = "세부 설정")
                            }
                        }
                    }
                    Spacer(modifier = Modifier.size(4.dp))
                }
            }
        }
    }
}