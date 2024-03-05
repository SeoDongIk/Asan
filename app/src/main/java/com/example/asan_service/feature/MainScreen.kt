package com.example.asan_service

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.asan_service.viewmodel.ImageViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController, viewModel: ImageViewModel) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var SoundisOn by remember { mutableStateOf(false) }
    var VibrateisOn by remember { mutableStateOf(false) }
    var PopUpisOn by remember { mutableStateOf(false) }
    val imageList by viewModel.imageList.observeAsState()
    var secret_box by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf("") }

    viewModel.getImageList()

    NavigationDrawer(
        drawerContent = {
            Row(
                modifier = Modifier
                    .background(Color.Gray)
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "설정",
                    color = Color.White,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                )
            }

            Row(
                modifier = Modifier
                    .background(Color(0x04, 0x61, 0x66))
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "소리 설정",
                    color = Color.White,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                )
                Spacer(modifier = Modifier.width(150.dp))
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(color = if (SoundisOn) Color.White else Color.Gray, shape = RoundedCornerShape(8.dp))
                        .clickable {
                            SoundisOn = !SoundisOn
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "ON",
                        color = if (SoundisOn) Color.Black else Color.White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(color = if (SoundisOn) Color.Gray else Color.White, shape = RoundedCornerShape(8.dp))
                        .clickable {
                            SoundisOn = !SoundisOn
                        },
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        text = "OFF",
                        color = if (SoundisOn) Color.White else Color.Black
                    )
                }
            }
            Row(
                modifier = Modifier
                    .background(Color(0x04, 0x61, 0x66))
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "진동설정",
                    color = Color.White,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                )
                Spacer(modifier = Modifier.width(150.dp))
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(color = if (VibrateisOn) Color.White else Color.Gray, shape = RoundedCornerShape(8.dp))
                        .clickable {
                            VibrateisOn = !VibrateisOn
                        },
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        text = "ON",
                        color = if (VibrateisOn) Color.Black else Color.White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(color = if (VibrateisOn) Color.Gray else Color.White, shape = RoundedCornerShape(8.dp))
                        .clickable {
                            VibrateisOn = !VibrateisOn
                        },
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        text = "OFF",
                        color = if (VibrateisOn) Color.White else Color.Black
                    )
                }
            }
            Row(
                modifier = Modifier
                    .background(Color(0x04, 0x61, 0x66))
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "팝업 설정",
                    color = Color.White,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                )
                Spacer(modifier = Modifier.width(150.dp))
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(color = if (PopUpisOn) Color.White else Color.Gray, shape = RoundedCornerShape(8.dp))
                        .clickable {
                            PopUpisOn = !PopUpisOn
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "ON",
                        color = if (PopUpisOn) Color.Black else Color.White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(color = if (PopUpisOn) Color.Gray else Color.White, shape = RoundedCornerShape(8.dp))
                        .clickable {
                            PopUpisOn = !PopUpisOn
                        },
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        text = "OFF",
                        color = if (PopUpisOn) Color.White else Color.Black
                    )
                }
            }

            Row(
                modifier = Modifier
                    .background(Color.Gray)
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "데이터",
                    color = Color.White,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                )
            }
            Row(
                modifier = Modifier
                    .background(Color(0x04, 0x61, 0x66))
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "통계량",
                    color = Color.White,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .clickable {
                            navController.navigate("StatisticScreen")
                        }
                        .weight(1f)
                        .padding(start = 8.dp)
                )
            }
            Row(
                modifier = Modifier
                    .background(Color.Gray)
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "위치 설정",
                    color = Color.White,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                )
            }
            Row(
                modifier = Modifier
                    .background(Color(0x04, 0x61, 0x66))
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "도면 설정",
                    color = Color.White,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .clickable {
                            navController.navigate("BackgroundSettingScreen")
                        }
                        .weight(1f)
                        .padding(start = 8.dp)
                )
            }
            Row(
                modifier = Modifier
                    .background(Color(0x04, 0x61, 0x66))
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "스캐너 설정",
                    color = Color.White,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .clickable {
                            navController.navigate("ScannerSettingScreen")
                        }
                        .weight(1f)
                        .padding(start = 8.dp)
                )
            }
        },
        drawerState = drawerState
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color(0x04, 0x61, 0x66),
                        titleContentColor = MaterialTheme.colorScheme.primary
                    ),
                    title = {
                        IconButton(
                            onClick = {
                                secret_box = true
                            },
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.amc_ke1_white),
                                modifier = Modifier.size(50.dp),
                                contentDescription = "Show First Screen"
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                drawerState.apply {
                                    if (isClosed) open() else close()
                                }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }
                )
            }
        ) {

            if (secret_box) {
                AlertDialog(
                    onDismissRequest = {
                        secret_box = false
                    },
                    title = {
                        Text("비밀 번호를 입력해주세요.",
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                            },
                    text = {
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
                    },
                    confirmButton = {
                        Button(onClick = {
                            if(text == "1234") {
                                navController.navigate("BackgroundSettingScreen")
                            } else {
                                text = ""
                            }
                            secret_box = false
                        },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0x04, 0x61, 0x66)),
                            ) {
                            Text("입력")
                        }
                    },
                    dismissButton = {
                        Button(onClick = {
                            text = ""
                            secret_box = false
                        },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0x04, 0x61, 0x66)),
                            ) {
                            Text("취소")
                        }
                    }
                )
            }

            ImageListDisplay(imageList = imageList ,navController =navController )

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
                            .fillMaxWidth()
                            .background(color = Color(0xFF, 0x57, 0xC1, 0x14))
                    ) {

                        Box(
                            modifier = Modifier
                                .fillMaxWidth(),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Button(
                                    onClick = { navController.navigate("AlarmScreen") },
                                    modifier = Modifier
                                        .padding(horizontal = 8.dp)
                                        .weight(1f)
                                ) {
                                    Text(text = "발생 알람")
                                }
                                Button(
                                    onClick = { navController.navigate("ConnectScreen") },
                                    modifier = Modifier
                                        .padding(horizontal = 8.dp)
                                        .weight(1f)
                                ) {
                                    Text(text = "연결 상태")
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.size(8.dp))
                }
            }



}


@Composable
fun ImageListDisplay(imageList: ImageDataList?, navController: NavController) {
    LazyColumn {
        imageList?.imageIds?.forEachIndexed { index, imageId ->
            item {
                Spacer(modifier = Modifier.size(4.dp))
                Column(
                    modifier = Modifier.padding(horizontal = 4.dp)
                        .border(BorderStroke(1.dp, Color(0x04, 0x61, 0x66)), shape = RoundedCornerShape(8.dp))
                        .padding(4.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = "도면 id : $imageId",
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        val imageName = imageList.imageNames.getOrNull(index) ?: "Unknown"
                        Text(
                            text = "도면 이름 : $imageName",
                            textAlign = TextAlign.Center
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        val imageName = imageList.imageNames.getOrNull(index) ?: "Unknown"
                        Button(
                            onClick = { navController.navigate("MoniteringScreen/$imageId?imageName=$imageName") },
                            modifier = Modifier.padding(4.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0x04, 0x61, 0x66)),
                        ) {
                            Text(text = "모니터링")
                        }
                    }
                }
            }
        }
    }
}

