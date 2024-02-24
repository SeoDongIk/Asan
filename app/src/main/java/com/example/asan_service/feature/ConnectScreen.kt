package com.example.asan_service

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.room.Room
import com.example.asan_service.core.AppDatabase
import com.example.asan_service.core.UserRepository
import com.example.asan_service.dao.WatchItemDao
import com.example.asan_service.data.User


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectScreen(navController : NavController, viewModel: MyViewModel) {

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
        val data = usersState.map {
            Triple(
                Pair(it.name, it.host),
                Pair(painterResource(id = R.drawable.heart), "80"),
                Pair(if(it.connected) painterResource(id = R.drawable.connect) else painterResource(id = R.drawable.notconnect), it.date)
            )
        }
        val data2 = listOf(
            Triple(
                Pair("첫번째 환자", "301호"),
                Pair(painterResource(id = R.drawable.heart), "80"),
                Pair(painterResource(id = R.drawable.notconnect), "Text 3")
            ),
            Triple(
                Pair("두번째 환자", "302호"),
                Pair(painterResource(id = R.drawable.heart), "80"),
                Pair(painterResource(id = R.drawable.notconnect), "Text 6")
            ),
            Triple(
                Pair("세번째 환자", "303호"),
                Pair(painterResource(id = R.drawable.heart), "80"),
                Pair(painterResource(id = R.drawable.connect), "Text 3")
            ),
            Triple(
                Pair("네번째 환자", "304호"),
                Pair(painterResource(id = R.drawable.heart), "80"),
                Pair(painterResource(id = R.drawable.connect), "Text 3")
            ),
            Triple(
                Pair("다섯번째 환자", "305호"),
                Pair(painterResource(id = R.drawable.heart), "80"),
                Pair(painterResource(id = R.drawable.connect), "Text 3")
            ),
            Triple(
                Pair("여섯번째 환자", "306호"),
                Pair(painterResource(id = R.drawable.heart), "80"),
                Pair(painterResource(id = R.drawable.connect), "Text 3")
            ),
        )
        MultiRowColumnTable(data = data)
    }
}


@Composable
fun MultiRowColumnTable(data: List<Triple<Pair<String, String>, Pair<Painter, String>, Pair<Painter, String>>>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(data) { row ->
            Row(
                modifier = Modifier
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
                        painter = row.third.first,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp)
                    )
                    Text(text = row.third.second)
                }
            }
        }
    }
}