package com.example.asan_service

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                title = {
                    Text(
                        "발생 알람 목록"
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
//        val data = listOf(
//            Person2("Mac Donut", "32", "남성", true),
//            Person2("John Doe", "30", "남성", false),
//            Person2("Jane Smith", "25", "여성", true),
//            Person2("Mike Johnson", "35", "남성", false)
//        )
//
//        LazyColumn(
//            modifier = Modifier.fillMaxSize()
//        ) {
//            item {
//                Spacer(modifier = Modifier.height(8.dp))
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceBetween
//                ) {
//                    repeat(3) {
//                        Box(
//                            modifier = Modifier
//                                .height(1.dp)
//                                .width(4.dp)
//                                .background(color = Color.Gray)
//                        )
//                    }
//                }
//                Spacer(modifier = Modifier.height(8.dp))
//            }
//            item {
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .background(Color.LightGray)
//                        .padding(vertical = 8.dp),
//                    verticalAlignment = Alignment.CenterVertically,
//                ) {
//                    Text(
//                        text = "이름",
//                        modifier = Modifier
//                            .weight(1f)
//                            .padding(horizontal = 4.dp)
//                            .background(color = Color.White, shape = RoundedCornerShape(4.dp))
//                            .height(48.dp)
//                            .align(Alignment.CenterVertically),
//                        textAlign = TextAlign.Center
//                    )
//                    Text(
//                        text = "나이",
//                        modifier = Modifier
//                            .weight(1f)
//                            .padding(horizontal = 4.dp)
//                            .background(color = Color.White, shape = RoundedCornerShape(4.dp))
//                            .height(48.dp)
//                            .align(Alignment.CenterVertically),
//                        textAlign = TextAlign.Center
//                    )
//                    Text(
//                        text = "연결 상태",
//                        modifier = Modifier
//                            .weight(1f)
//                            .padding(horizontal = 4.dp)
//                            .background(color = Color.White, shape = RoundedCornerShape(4.dp))
//                            .height(48.dp)
//                            .align(Alignment.CenterVertically),
//                        textAlign = TextAlign.Center
//                    )
//                }
//            }
//            item {
//                Spacer(modifier = Modifier.height(8.dp))
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceBetween
//                ) {
//                    repeat(3) {
//                        Box(
//                            modifier = Modifier
//                                .height(1.dp)
//                                .width(4.dp)
//                                .background(color = Color.Gray)
//                        )
//                    }
//                }
//                Spacer(modifier = Modifier.height(8.dp))
//            }
//
//            val (connectedPersons, disconnectedPersons) = data.partition { it.isConnected }
//
//            items(disconnectedPersons.size) { personIndex ->
//                val person = disconnectedPersons[personIndex]
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .background(Color(255, 192, 203))
//                        .padding(vertical = 8.dp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Text(
//                        text = person.name,
//                        modifier = Modifier
//                            .weight(1f)
//                            .padding(horizontal = 4.dp)
//                            .background(color = Color.White, shape = RoundedCornerShape(4.dp))
//                            .height(48.dp)
//                            .align(Alignment.CenterVertically),
//                        textAlign = TextAlign.Center
//                    )
//                    Text(
//                        text = person.age,
//                        modifier = Modifier
//                            .weight(1f)
//                            .padding(horizontal = 4.dp)
//                            .background(color = Color.White, shape = RoundedCornerShape(4.dp))
//                            .height(48.dp)
//                            .align(Alignment.CenterVertically),
//                        textAlign = TextAlign.Center
//                    )
//                    Text(
//                        text = person.isConnected.toString(),
//                        modifier = Modifier
//                            .weight(1f)
//                            .padding(horizontal = 4.dp)
//                            .background(color = Color.White, shape = RoundedCornerShape(4.dp))
//                            .height(48.dp)
//                            .align(Alignment.CenterVertically),
//                        textAlign = TextAlign.Center
//                    )
//                }
//                if (personIndex < disconnectedPersons.size - 1) {
//                    Spacer(modifier = Modifier.height(8.dp))
//                    Row(
//                        modifier = Modifier.fillMaxWidth(),
//                        horizontalArrangement = Arrangement.SpaceBetween
//                    ) {
//                        repeat(3) {
//                            Box(
//                                modifier = Modifier
//                                    .height(1.dp)
//                                    .width(4.dp)
//                                    .background(color = Color.Gray)
//                            )
//                        }
//                    }
//                    Spacer(modifier = Modifier.height(8.dp))
//                }
//            }
//            item {
//                Spacer(modifier = Modifier.height(8.dp))
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceBetween
//                ) {
//                    repeat(3) {
//                        Box(
//                            modifier = Modifier
//                                .height(1.dp)
//                                .width(4.dp)
//                                .background(color = Color.Gray)
//                        )
//                    }
//                }
//                Spacer(modifier = Modifier.height(8.dp))
//            }
//            items(connectedPersons.size) { personIndex ->
//                val person = connectedPersons[personIndex]
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .background(Color.LightGray)
//                        .padding(vertical = 8.dp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Text(
//                        text = person.name,
//                        modifier = Modifier
//                            .weight(1f)
//                            .padding(horizontal = 4.dp)
//                            .background(color = Color.White, shape = RoundedCornerShape(4.dp))
//                            .height(48.dp)
//                            .align(Alignment.CenterVertically),
//                        textAlign = TextAlign.Center
//                    )
//                    Text(
//                        text = person.age,
//                        modifier = Modifier
//                            .weight(1f)
//                            .padding(horizontal = 4.dp)
//                            .background(color = Color.White, shape = RoundedCornerShape(4.dp))
//                            .height(48.dp)
//                            .align(Alignment.CenterVertically),
//                        textAlign = TextAlign.Center
//                    )
//                    Text(
//                        text = person.isConnected.toString(),
//                        modifier = Modifier
//                            .weight(1f)
//                            .padding(horizontal = 4.dp)
//                            .background(color = Color.White, shape = RoundedCornerShape(4.dp))
//                            .height(48.dp)
//                            .align(Alignment.CenterVertically),
//                        textAlign = TextAlign.Center
//                    )
//                }
//                if (personIndex < connectedPersons.size - 1) {
//                    Spacer(modifier = Modifier.height(8.dp))
//                    Row(
//                        modifier = Modifier.fillMaxWidth(),
//                        horizontalArrangement = Arrangement.SpaceBetween
//                    ) {
//                        repeat(3) {
//                            Box(
//                                modifier = Modifier
//                                    .height(1.dp)
//                                    .width(4.dp)
//                                    .background(color = Color.Gray)
//                            )
//                        }
//                    }
//                    Spacer(modifier = Modifier.height(8.dp))
//                }
//            }
//        }

    }
}

//data class AlarmData(val name: String, val age: String, val gender: String, val isConnected: Boolean)
