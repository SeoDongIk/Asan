package com.example.asan_service

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlaramScreen(navController : NavController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0x04, 0x61, 0x66),
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                title = {
                    Text(
                        "발생 알람 목록",
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
        val data = listOf(
            listOf("알람 종류", "이름", "발생 시간", "발생 위치"),
            listOf("낙상", "홍길동", "2분전", "1704"),
            listOf("낙상", "홍길동", "2분전", "1704"),
            listOf("낙상", "홍길동", "2분전", "1704"),
            listOf("낙상", "홍길동", "2분전", "1704"),
            listOf("낙상", "홍길동", "2분전", "1704"),
            listOf("낙상", "홍길동", "2분전", "1704"),
            listOf("낙상", "홍길동", "2분전", "1704"),
            listOf("낙상", "홍길동", "2분전", "1704"),
            listOf("낙상", "홍길동", "2분전", "1704"),
            listOf("낙상", "홍길동", "2분전", "1704"),
            listOf("낙상", "홍길동", "2분전", "1704"),
        )
        MultiRowColumnTable(data = data)
    }
}

@Composable
fun MultiRowColumnTable(data: List<List<String>>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(data) { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                row.forEach { text ->
                    Text(
                        text = text,
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}