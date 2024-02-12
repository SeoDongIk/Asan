package com.example.asan_service

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
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
            Triple(
                Pair("첫번째 환자", "301호"),
                Pair(painterResource(id = R.drawable.ic_launcher_background), "80"),
                Pair(painterResource(id = R.drawable.ic_launcher_background), "Text 3")
            ),
            Triple(
                Pair("두번째 환자", "302호"),
                Pair(painterResource(id = R.drawable.ic_launcher_background), "80"),
                Pair(painterResource(id = R.drawable.ic_launcher_background), "Text 6")
            ),
            Triple(
                Pair("세번째 환자", "303호"),
                Pair(painterResource(id = R.drawable.ic_launcher_background), "80"),
                Pair(painterResource(id = R.drawable.ic_launcher_background), "Text 3")
            ),
            Triple(
                Pair("네번째 환자", "304호"),
                Pair(painterResource(id = R.drawable.ic_launcher_background), "80"),
                Pair(painterResource(id = R.drawable.ic_launcher_background), "Text 3")
            ),
            Triple(
                Pair("다섯번째 환자", "305호"),
                Pair(painterResource(id = R.drawable.ic_launcher_background), "80"),
                Pair(painterResource(id = R.drawable.ic_launcher_background), "Text 3")
            ),
            Triple(
                Pair("여섯번째 환자", "306호"),
                Pair(painterResource(id = R.drawable.ic_launcher_background), "80"),
                Pair(painterResource(id = R.drawable.ic_launcher_background), "Text 3")
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
                    verticalAlignment = Alignment.CenterVertically
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