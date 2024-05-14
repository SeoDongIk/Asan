package com.example.asan_service

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

import androidx.compose.runtime.livedata.observeAsState
import com.example.asan_service.data.User
import com.example.asan_service.util.ScannerScreen
import com.example.asan_service.viewmodel.ConnectScreenViewModel
import com.example.asan_service.viewmodel.PasswordViewModel
import com.example.asan_service.viewmodel.ScannerSettingViewModel
import java.io.ByteArrayOutputStream



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerSettingScreen(navController: NavController, viewModel: ConnectScreenViewModel, passwordViewModel: PasswordViewModel) {
    val usersState by viewModel.sortedUsers.observeAsState(initial = emptyList())
    var screen by remember { mutableStateOf(ScannerScreen.ConnectedWatches) } // 초기 화면 상태는 '전체 워치'
    val connectedUsers = usersState.filter { it.connected } // '연결된 워치'만 필터링

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0x04, 0x61, 0x66),
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                title = {
                    Text("스캐너 설정", color = Color.White)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("MainScreen") }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                }
            )
        }
    ) {
        Column {
            // 탭 버튼 배치
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x04, 0x61, 0x66)) // 배경색 지정
            ) {
                Button(
                    onClick = { screen = ScannerScreen.ConnectedWatches },
                    modifier = Modifier
                        .weight(1f),
                    shape = RectangleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0x04, 0x61, 0x66),
                        contentColor = if (screen == ScannerScreen.ConnectedWatches) Color.White else Color.Black
                    )
                ) {
                    Text("연결된 워치")
                }
                Button(
                    onClick = { screen = ScannerScreen.AllWatches },
                    modifier = Modifier
                        .weight(1f),
                    shape = RectangleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0x04, 0x61, 0x66),
                        contentColor = if (screen == ScannerScreen.AllWatches) Color.White  else Color.Black
                    )
                ) {
                    Text("전체 워치")
                }

            }

            // 화면 내용
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
            ) {
                val currentList = if (screen == ScannerScreen.AllWatches) usersState else connectedUsers
                if (currentList.isEmpty()) {
                    item {
                        Text(
                            "연결된 워치가 없습니다. 연결 상태를 확인해주세요.",
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    items(currentList) { user ->
                        UserRow(user, navController)
                    }
                }
            }
        }
    }
}

@Composable
fun UserRow(user: User, navController: NavController) {
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
            Text(text = "watch id: ${user.watchId}")
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "환자 이름: ${user.name}")
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = { navController.navigate("WatchSettingScreen/${user.watchId}/${user.name}/${user.connected}") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0x04, 0x61, 0x66))
            ) {
                Text("세부 설정")
            }
        }
    }
    Spacer(modifier = Modifier.size(4.dp))
}

