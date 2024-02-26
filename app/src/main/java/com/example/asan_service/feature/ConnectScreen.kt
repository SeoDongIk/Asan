package com.example.asan_service

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.asan_service.viewmodel.ConnectScreenViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectScreen(navController : NavController, viewModel: ConnectScreenViewModel) {
    val usersState by viewModel.users.observeAsState(initial = emptyList())

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0x04, 0x61, 0x66),
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                title = {
                    Text(
                        "연결 상태",
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
    ) {
        val sortedList = usersState
        val data = sortedList.map {
            Triple(
                Pair(it.name, it.host),
                Pair(painterResource(id = R.drawable.heart), "80"),
                Triple(it.connected,if(it.connected) painterResource(id = R.drawable.connect) else painterResource(id = R.drawable.notconnect), ((System.currentTimeMillis()-it.date) / (1000*60)).toString() + "분")
            )
        }

        MultiRowColumnTable(data = data)
    }
}


@Composable
fun MultiRowColumnTable(data: List<Triple<Pair<String, String>, Pair<Painter, String>, Triple<Boolean, Painter, String>>>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(data) { row ->
            Row(
                modifier = Modifier
                    .background(if (row.third.first) Color.White else Color(0xFF, 0x57, 0xC1, 0x14))
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 첫 번째 열: 텍스트 2개 세로 배치
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = row.first.first)
                    Text(text = row.first.second)
                }

                // 두 번째 열: 아이콘 1개와 텍스트 1개 가로 배치
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = row.second.first,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = row.second.second)
                }

                // 세 번째 열: 아이콘 1개와 텍스트 1개 세로 배치
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = row.third.second,
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.Transparent)
                    )
                    Text(text = if(row.third.first) "-" else row.third.third)
                }
            }
        }
    }
}