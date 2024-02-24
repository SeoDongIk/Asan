package com.example.asan_service.feature

import android.graphics.Paint.Align
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchSettingScreen(navController : NavController, text : String) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0x04, 0x61, 0x66),
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                title = {
                    Text(
                        text,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
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
        var text by remember { mutableStateOf("") }
        var secret_box by remember { mutableStateOf(false) }
        var selectedItem by remember { mutableStateOf<Item?>(null) }
        val items = listOf(
            Item("Item 1", "Room A", "12345", "Jan 2024 - Feb 2024"),
            Item("Item 2", "Room B", "67890", "Feb 2024 - Mar 2024"),
            Item("Item 3", "Room C", "24680", "Mar 2024 - Apr 2024"),
            Item("Item 1", "Room A", "12345", "Jan 2024 - Feb 2024"),
            Item("Item 2", "Room B", "67890", "Feb 2024 - Mar 2024"),
            Item("Item 3", "Room C", "24680", "Mar 2024 - Apr 2024"),
            Item("Item 1", "Room A", "12345", "Jan 2024 - Feb 2024"),
            Item("Item 2", "Room B", "67890", "Feb 2024 - Mar 2024"),
            Item("Item 3", "Room C", "24680", "Mar 2024 - Apr 2024"),
        )

        if (secret_box) {
            AlertDialog(
                onDismissRequest = {
                    secret_box = false
                },
                title = {
                    Text("이름을 변경합니다",
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                },
                text = {
                },
                confirmButton = {
                    Button(onClick = {
                        //
                        text = ""
                        secret_box = false
                    },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0x04, 0x61, 0x66)),
                    ) {
                        Text("확인")
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        //
                        secret_box = false
                    },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0x04, 0x61, 0x66)),
                        ) {
                        Text("취소")
                    }
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .background(Color(0x04, 0x61, 0x66))
                    .fillMaxWidth()
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "이름 설정",
                    color = Color.White,
                    modifier = Modifier
                        .padding(4.dp),
                    textAlign = TextAlign.Center
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "변경하실 이름을 입력해주세요",
                    modifier = Modifier
                        .padding(4.dp),
                    textAlign = TextAlign.Center
                )
            }
            BasicTextField(
                value = text,
                onValueChange = { newText ->
                    text = newText
                },
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = Color(0x04, 0x61, 0x66),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .fillMaxWidth()
                    .padding(16.dp),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                )
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Button(
                onClick = { secret_box = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0x04, 0x61, 0x66)),
            ) {
                Text(text = "입력")
            }
            }
            Row(
                modifier = Modifier
                    .background(Color(0x04, 0x61, 0x66))
                    .fillMaxWidth()
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "데이터 추가 설정",
                    color = Color.White,
                    modifier = Modifier
                        .padding(4.dp),
                    textAlign = TextAlign.Center
                )
            }
            DropdownLayout2(
                items = items,
                selectedItem = selectedItem,
                onItemSelected = { selectedItem = it }
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${selectedItem?.name} ${selectedItem?.room}",
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = selectedItem?.watchId ?: "N/A",
                    modifier = Modifier.padding(start = 16.dp)
                )
                Text(
                    text = selectedItem?.measuredPeriod ?: "N/A",
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
            Row(
                modifier = Modifier
                    .background(Color(0x04, 0x61, 0x66))
                    .fillMaxWidth()
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "데이터 추가",
                    color = Color.White,
                    modifier = Modifier
                        .padding(4.dp),
                    textAlign = TextAlign.Center
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { navController.navigate("WatchSettingScreen") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0x04, 0x61, 0x66)),
                ) {
                    Text(text = "시작")
                }
                Button(
                    onClick = { navController.navigate("WatchSettingScreen") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0x04, 0x61, 0x66)),
                ) {
                    Text(text = "종료")
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "00 : 00",
                    modifier = Modifier
                        .padding(4.dp),
                    textAlign = TextAlign.Center
                )
            }
            Row(
                modifier = Modifier
                    .background(Color(0x04, 0x61, 0x66))
                    .fillMaxWidth()
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "기기 정보",
                    color = Color.White,
                    modifier = Modifier
                        .padding(4.dp),
                    textAlign = TextAlign.Center
                )
            }
            Row(
                modifier = Modifier
                    .background(Color.LightGray)
                    .fillMaxWidth()
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "기종 : ",
                    color = Color.White,
                    modifier = Modifier
                        .padding(4.dp),
                    textAlign = TextAlign.Center
                )
            }
            Row(
                modifier = Modifier
                    .background(Color.LightGray)
                    .fillMaxWidth()
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "기기ID : ",
                    color = Color.White,
                    modifier = Modifier
                        .padding(4.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun DropdownLayout2(
    items: List<Item>,
    selectedItem: Item?,
    onItemSelected: (Item) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Column(
    ) {
        Box(
            modifier = Modifier
                .background(Color.LightGray)
                .fillMaxWidth()
                .height(56.dp)
                .clickable { expanded = true },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "장소 선택",
                color = Color.White,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Dropdown Arrow",
                tint = Color.White,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(48.dp)
            )
        }

        if (expanded) {
            AlertDialog(
                onDismissRequest = { expanded = false },
                title = { Text("Select an item") },
                text = {
                    Column(
                        modifier = Modifier.verticalScroll(scrollState)
                    ) {
                        items.forEach { item ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onItemSelected(item)
                                        expanded = false
                                    }
                                    .padding(vertical = 8.dp, horizontal = 16.dp)
                            ) {
                                Text(text = "Name: ${item.name}")
                                Text(text = "Room: ${item.room}")
                                Text(text = "Watch ID: ${item.watchId}")
                                Text(text = "Measured Period: ${item.measuredPeriod}")
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = { expanded = false }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    Button(onClick = { expanded = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}