package com.example.asan_service

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectScreen(navController : NavController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                title = {
                    Text(
                        "연결 상태"
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
        val data = listOf(
            ConnectData("Mac Donut", 304, 79, true, 0),
            ConnectData("John Doe", 301, 87, false, 3),
            ConnectData("Jane Smith", 302, 61, true, 2),
            ConnectData("Mike Johnson", 303, 92, false, 5)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.LightGray)
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "이름",
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp)
                            .background(color = Color.White, shape = RoundedCornerShape(4.dp))
                            .height(48.dp)
                            .align(Alignment.CenterVertically),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "심박수",
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp)
                            .background(color = Color.White, shape = RoundedCornerShape(4.dp))
                            .height(48.dp)
                            .align(Alignment.CenterVertically),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "연결 상태",
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp)
                            .background(color = Color.White, shape = RoundedCornerShape(4.dp))
                            .height(48.dp)
                            .align(Alignment.CenterVertically),
                        textAlign = TextAlign.Center
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    repeat(3) {
                        Box(
                            modifier = Modifier
                                .height(1.dp)
                                .width(4.dp)
                                .background(color = Color.Gray)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            val (connectedPersons, disconnectedPersons) = data.partition { it.isConnected }

            items(disconnectedPersons.size) { personIndex ->
                val person = disconnectedPersons[personIndex]
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(255, 192, 203))
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp)
                            .background(color = Color.White, shape = RoundedCornerShape(4.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = person.name,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 8.dp) // 좌우 여백 추가
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp)
                            .background(color = Color.White, shape = RoundedCornerShape(4.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_launcher_background), // 아이콘 리소스를 여기에 넣으세요.
                            contentDescription = null, // contentDescription 설정
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = person.heartRate.toString(),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(start = 4.dp) // 텍스트와 아이콘 사이의 여백 추가
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp)
                            .background(color = Color.White, shape = RoundedCornerShape(4.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_launcher_background), // 아이콘 리소스를 여기에 넣으세요.
                            contentDescription = null, // contentDescription 설정
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                if (personIndex < disconnectedPersons.size - 1) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        repeat(3) {
                            Box(
                                modifier = Modifier
                                    .height(1.dp)
                                    .width(4.dp)
                                    .background(color = Color.Gray)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    repeat(3) {
                        Box(
                            modifier = Modifier
                                .height(1.dp)
                                .width(4.dp)
                                .background(color = Color.Gray)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            items(connectedPersons.size) { personIndex ->
                val person = connectedPersons[personIndex]
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(255, 192, 203))
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp)
                            .background(color = Color.White, shape = RoundedCornerShape(4.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = person.name,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 8.dp) // 좌우 여백 추가
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp)
                            .background(color = Color.White, shape = RoundedCornerShape(4.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_launcher_background), // 아이콘 리소스를 여기에 넣으세요.
                            contentDescription = null, // contentDescription 설정
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = person.heartRate.toString(),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(start = 4.dp) // 텍스트와 아이콘 사이의 여백 추가
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp)
                            .background(color = Color.White, shape = RoundedCornerShape(4.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_launcher_background), // 아이콘 리소스를 여기에 넣으세요.
                            contentDescription = null, // contentDescription 설정
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                if (personIndex < connectedPersons.size - 1) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        repeat(3) {
                            Box(
                                modifier = Modifier
                                    .height(1.dp)
                                    .width(4.dp)
                                    .background(color = Color.Gray)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

    }
}

data class ConnectData(val name: String, val roomNumber: Int, val heartRate: Int, val isConnected: Boolean, val errorTime: Int)



