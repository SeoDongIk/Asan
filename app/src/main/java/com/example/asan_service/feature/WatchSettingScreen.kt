package com.example.asan_service.feature


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.asan_service.BeaconCount
import com.example.asan_service.MyWebSocketService
import com.example.asan_service.PositionList
import com.example.asan_service.PositionNameData
import com.example.asan_service.R
import com.example.asan_service.viewmodel.ConnectScreenViewModel
import com.example.asan_service.viewmodel.ImageViewModel
import com.example.asan_service.viewmodel.ScannerSettingViewModel
import com.example.asan_service.viewmodel.WatchSettingViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchSettingScreen(navController: NavHostController, viewModel: ImageViewModel, connectScreenViewModel: ConnectScreenViewModel, watchSettingViewModel: WatchSettingViewModel) {
    val watchId = navController.currentBackStackEntry?.arguments?.getString("watchId")?.toLong()
    val name = navController.currentBackStackEntry?.arguments?.getString("watchName")
    val connection = navController.currentBackStackEntry?.arguments?.getString("connected")?.toBoolean()
    var secret_box by remember { mutableStateOf(false) }
    var collectedMinutes by remember { mutableStateOf(0) } // 수집된 시간(분)
    var collectedSeconds by remember { mutableStateOf(0) } // 수집된 시간(초)
    val positionList = viewModel.positionList.observeAsState().value
    var userInput by remember { mutableStateOf(name) }
    var timerActive by remember { mutableStateOf(false) }
    var timerDurationSeconds by remember { mutableStateOf(0) }
    var timerRemainingSeconds by remember { mutableStateOf(0) }
    var showConfirmationDialog by remember { mutableStateOf(false) }
    val endTimes by viewModel.endTimes.collectAsState()
    val collectPosition by viewModel.collectPosition.collectAsState()
    var savedEndTime = endTimes[watchId]
    val beaconCounts = watchSettingViewModel.beaconCountList.observeAsState(listOf()).value




    LaunchedEffect(watchId) {
        watchId?.let {
            viewModel.getCollectionStatus(it.toString())
            watchSettingViewModel.getCountBeacon()
        }
        Log.d("savedEndTime",savedEndTime.toString())
    }


    val context = LocalContext.current
    val intent = remember { Intent(context, MyWebSocketService::class.java) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0x04, 0x61, 0x66),
                    titleContentColor = Color.White
                ),
                title = {
                    Text(
                        text = if (watchId != null) {
                            "Watch id : " + watchId.toString()
                        } else {
                            ""
                        },
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        // Make sure this navigation route is correctly handled in your NavHost
                        navController.navigate("ScannerSettingScreen")
                    }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                }, actions = {
                    // 이 부분에 삭제 아이콘을 추가합니다.
                    IconButton(onClick = {
                        if (watchId != null) {

                          secret_box = true

                        }
                    }) {
                        Text(
                            text = "삭제",
                            color = Color(0xFFFFA500)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        if (secret_box) {

            AlertDialog(
                onDismissRequest = {
                    secret_box = false
                },
                title = {
                    Text(watchId.toString()+"번 워치를 삭제하시겠습니까?.",
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                },

                confirmButton = {
                    Button(onClick = {
                        if(watchId != null) {
                            watchSettingViewModel.deleteWatch(watchId.toLong())
                            navController.navigate("ScannerSettingScreen")
                        }
                        secret_box = false
                    },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0x04, 0x61, 0x66)),
                    ) {
                        Text("확인")
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        secret_box = false
                    },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0x04, 0x61, 0x66)),
                    ) {
                        Text("취소")
                    }
                }
            )
        }

        var selectedItem by remember { mutableStateOf<String?>(null) }
        var selectedImageId by remember { mutableStateOf<Long?>(null) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Scaffold로부터 제공받은 내부 패딩 적용
//                .padding(vertical = 10.dp)

        ) {
            Row(
                modifier = Modifier
                    .background(Color.LightGray)
                    .fillMaxWidth(), // 텍스트의 배경색을 수평적으로 전체 영역에 적용
                verticalAlignment = Alignment.CenterVertically // 텍스트를 세로 중앙에 정렬
            ) {

                Text(
                    text = "사용자 지정", // 텍스트 내용
                    color = Color.Black, // 텍스트 색상
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 3.dp) // 내부 패딩
                )


            }

            Spacer(modifier = Modifier.height(16.dp))


            BasicTextField(
                value = userInput!!, // 현재 상태 변수를 value로 바인딩
                onValueChange = { newUserInput ->
                    userInput = newUserInput // 사용자 입력이 변경될 때마다 상태 업데이트
                },
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = Color(0x04, 0x61, 0x66),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .fillMaxWidth()
                    .padding(4.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                decorationBox = { innerTextField -> // 입력 필드의 데코레이션
                    Row(
                        Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        innerTextField() // BasicTextField의 기본 동작
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (watchId != null) {
                        viewModel.changeName(watchId, userInput!!, "none")
                        connectScreenViewModel.changeNickName(watchId.toString(), userInput!!)



                    }
                    showConfirmationDialog = true
                    context.startService(intent)


                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0x04, 0x61, 0x66)
                ),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally) // 버튼을 세로 중앙에 정렬
                    .padding(start = 8.dp) // 버튼과 텍스트 필드 사이의 간격

            ) {
                Text("사용자 이름 지정")
            }


            if (showConfirmationDialog) {
                AlertDialog(
                    onDismissRequest = {
                        showConfirmationDialog = false
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showConfirmationDialog = false
                            }
                        ) {
                            Text("확인")
                        }
                    },
                    title = { Text("변경 완료") },
                    text = { Text("사용자 이름이 '${userInput}'(으)로 변경되었습니다.") }
                )
            }


            Spacer(modifier = Modifier.height(16.dp))


            if (connection == true) {
                Row(
                    modifier = Modifier
                        .background(Color.LightGray)
                        .fillMaxWidth(), // 텍스트의 배경색을 수평적으로 전체 영역에 적용
                    verticalAlignment = Alignment.CenterVertically // 텍스트를 세로 중앙에 정렬
                ) {

                    Text(
                        text = "데이터 추가 설정", // 텍스트 내용
                        color = Color.Black, // 텍스트 색상
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 3.dp) // 내부 패딩
                    )
                }

                DropdownLayout1(
                    items = positionList,
                    selectedItem = selectedItem,
                    timerActive = timerActive,
                    selectedImageId = selectedImageId

                ) { name, imageId ->
                    selectedItem = name
                    selectedImageId = imageId
                }


                Spacer(modifier = Modifier.height(16.dp)) // Dropdown and buttons spacing

                Row(
                    modifier = Modifier
                        .background(Color.LightGray)
                        .fillMaxWidth(), // 텍스트의 배경색을 수평적으로 전체 영역에 적용
                    verticalAlignment = Alignment.CenterVertically // 텍스트를 세로 중앙에 정렬
                ) {
                    Text(
                        text = "데이터 추가", // 텍스트 내용
                        color = Color.Black, // 텍스트 색상
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 3.dp) // 내부 패딩
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                var selectedMinute by remember { mutableStateOf(0) }

                LaunchedEffect(timerActive) {
                    if (timerActive && (savedEndTime == 0L || savedEndTime == null)) {
                        timerDurationSeconds = selectedMinute // 분을 초로 변환
                        timerRemainingSeconds = timerDurationSeconds
                    }
                    while (timerActive && timerRemainingSeconds > 0) {
                        delay(1000) // 1초 기다림
                        // 남은 시간이 0 초과일 때만 감소
                        if (timerRemainingSeconds > 0) {
                            timerRemainingSeconds -= 1
                        } else {
                            break // 더 이상 반복하지 않음
                        }
                    }
                    if (watchId != null) {
                        viewModel.deleteEndTime(watchId)
                        viewModel.deletePosition(watchId)
                    }
                    timerActive = false
//                    watchSettingViewModel.getCountBeacon()
                }


                val currentTime = System.currentTimeMillis()

                if (savedEndTime == null) {

                    if (!timerActive) {
                        TimePicker(onMinuteSelected = { minute ->
                            selectedMinute = minute * 60
                        })
                    } else {
                        selectedItem?.let { TimerDisplay(timerRemainingSeconds, it) }
                    }
                } else {
                    timerActive = true
                    val remainTimeMillis = savedEndTime!!.minus(currentTime)
                    timerRemainingSeconds = (remainTimeMillis / 1000).toInt()
                    collectPosition[watchId]?.let { it1 ->
                        TimerDisplay(
                            timerRemainingSeconds,
                            it1
                        )
                    }
                }


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            val endTime = System.currentTimeMillis() + (selectedMinute * 1000)
                            selectedImageId?.let {
                                viewModel.insertState(
                                    watchId.toString(),
                                    it, selectedItem.toString(), endTime
                                )
                            }
                            timerActive = true
                            collectedMinutes = 0 // 초기화
                            collectedSeconds = 0 // 초기화
                            // 수집 중인 시간을 업데이트하는 로직을 추가할 수 있습니다.
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0x04, 0x61, 0x66)
                        ),
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .weight(1f),
                        enabled = selectedItem != null && !timerActive
                    ) {
                        Text(text = "비콘 수집")
                    }
                    Button(
                        onClick = {
                            timerActive = false
                            if (watchId != null) {
                                viewModel.deleteEndTime(watchId)
                            }
                            viewModel.deleteState(watchId.toString())
                        }, // 버튼 클릭 시 수집 중단
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0x04, 0x61, 0x66)
                        ),
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .weight(1f),
                    ) {
                        Text(text = "수집 중지")
                        watchSettingViewModel.getCountBeacon()
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            else{


                Row(
                    modifier = Modifier
                        .background(color = Color(0xFFFFA500)) // 배경색 적용
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically // 텍스트를 세로 중앙에 정렬
                ) {
                    Text(
                        text = "현재 서버와 연결된 워치가 아닙니다.",
                        style = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center), // 여기도 동일하게 적용
                        modifier = Modifier
                            .fillMaxWidth() // 너비를 최대로 설정하여 중앙 정렬 가능하게 함
                            .padding(4.dp)
                    )
                }


            }

                if (beaconCounts != null) {
                    if (connection != null) {
                        BeaconTable(beaconCounts , watchSettingViewModel,connection)
                    }
                }
            }


        }


    }



@Composable
fun DropdownLayout1(
    items: List<PositionList>?,
    selectedItem: String?,
    selectedImageId : Long?,
    timerActive : Boolean,
    onItemSelected: (String, Long?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    val context = LocalContext.current
    val intent = remember { Intent(context, MyWebSocketService::class.java) }

    Column(
        modifier = Modifier.padding(horizontal = 5.dp)

    ) {
        BoxWithConstraints(
            modifier = Modifier
                .background(Color.White)
                .fillMaxWidth()
                .height(30.dp)
                .clickable {
                    if (!timerActive) {
                        expanded = true
                    }
                }
        ) {
            val width = constraints.maxWidth.toFloat() // 컴포저블의 최대 너비
            val height = constraints.maxHeight.toFloat() // 컴포저블의 최대 높이

            Canvas(modifier = Modifier.fillMaxSize()) {
                // 가장 아랫변에만 테두리 그리기
                drawLine(
                    color = Color.Black, // 테두리 색상: 검정
                    start = Offset(x = 0f, y = height),
                    end = Offset(x = width, y = height)
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween // 텍스트는 왼쪽 끝, 아이콘은 오른쪽 끝 정렬
            ) {
                Text(
                    text = selectedItem?.toString() ?: "장소 선택",
                    color = Color.Black,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Left,
                    modifier = Modifier.padding(end = 8.dp) // Text를 오른쪽으로 약간 이동
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown Arrow",
                    tint = Color.Black,
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        if (expanded) {
            AlertDialog(
                onDismissRequest = { expanded = false },
                title = { Text("비콘 데이터를 수집할 장소를 지정해주세요",fontSize = 18.sp) },
                text = {
                    Column(modifier = Modifier.verticalScroll(scrollState)) {
                        items?.forEach { item ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onItemSelected(item.position, item.imageId)
                                        expanded = false
                                        context.startService(intent)
                                    }
                                    .padding(vertical = 8.dp, horizontal = 16.dp)
                            ) {

                                Text(text = "Room: ${item.position}")

                            }
                        }
                    }
                },
                confirmButton = {
                },
                dismissButton = {
                    Button(onClick = { expanded = false }) {
                        Text("닫기")
                    }
                }
            )
        }
    }
}


@Composable
fun TimePicker(onMinuteSelected: (Int) -> Unit) {
    // Assume that selectedMinute is a state defined in the parent composable
    var selectedMinute by remember { mutableStateOf(0) }

    Column {
        TimePickerColumn(
            range = 0..16, // This defines the range of minutes that can be selected
            label = "분", // Label for the picker
            onValueChange = { value ->
                selectedMinute = value // Update the state with the new selected minute
                onMinuteSelected(value) // Invoke the callback with the selected minute
            }
        )
    }
}

@Composable
fun TimePickerColumn(
    range: IntRange,
    label: String,
    onValueChange: (Int) -> Unit
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val centeredItemIndex = remember {
        derivedStateOf {
            val visibleItemsInfo = listState.layoutInfo.visibleItemsInfo
            if (visibleItemsInfo.isNotEmpty()) {
                val viewportCenter = listState.layoutInfo.viewportEndOffset / 2
                visibleItemsInfo.minByOrNull { kotlin.math.abs((it.offset + it.size / 2) - viewportCenter) }?.index
            } else null
        }
    }

    LaunchedEffect(centeredItemIndex.value) {
        // Update selectedMinute when the centered item changes
        centeredItemIndex.value?.let { index ->
            onValueChange(range.elementAt(index))
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .height(150.dp)
            .wrapContentWidth()
    ) {
        items(range.toList()) { value ->
            val backgroundColor = if (value == centeredItemIndex.value) Color(0xFFFFA500) // 주황색으로 변경
            else Color.Transparent // 기본 배경색은 투명

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(color = backgroundColor) // 배경색 적용
                    .clickable {
                        // Smooth scroll to the clicked item so it gets centered
                        coroutineScope.launch {
                            listState.animateScrollToItem(index = value)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "$value $label", fontSize = 20.sp)
            }
        }
    }
}



@Composable
fun TimerDisplay(seconds: Int,selectedItem : String) {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    val context = LocalContext.current

    Text(
        text = String.format("%02d:%02d", minutes, remainingSeconds),
        style = MaterialTheme.typography.displayLarge.copy(textAlign = TextAlign.Center), // 텍스트 스타일에 textAlign 추가
        modifier = Modifier
            .fillMaxWidth() // 너비를 최대로 설정하여 중앙 정렬 가능하게 함
            .padding(16.dp)
    )

    Text(
        text = "${selectedItem} 위치의 비콘 데이터를 수집 중 입니다.",
        style = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center), // 여기도 동일하게 적용
        modifier = Modifier
            .fillMaxWidth() // 너비를 최대로 설정하여 중앙 정렬 가능하게 함
            .padding(4.dp)
    )


//    fun showNotification(context: Context,selectedItem : String) {
//        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel("timer_done", "Timer Done", NotificationManager.IMPORTANCE_DEFAULT)
//            notificationManager.createNotificationChannel(channel)
//            Notification.Builder(context, "timer_done")
//        } else {
//            Notification.Builder(context)
//        }
//
//        builder.setContentTitle("타이머 종료")
//            .setContentText(selectedItem +" 위치의 비콘 수집이 종료되었습니다.")
//            .setSmallIcon(R.drawable.amc_ke1_white)
//            .setPriority(Notification.PRIORITY_DEFAULT)
//
//        notificationManager.notify(1, builder.build())
//    }
//
//    if(remainingSeconds == 0){
//        showNotification(context,selectedItem)
//    }

}

@Composable
fun BeaconTable(beaconCounts: List<BeaconCount>, watchSettingViewModel: WatchSettingViewModel, connection : Boolean) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedPosition by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .background(Color.LightGray)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "비콘 수집량",
                color = Color.Black,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 3.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0x04, 0x61, 0x66)),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Position",
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )
            Text(
                "Count",
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )

            Spacer(modifier = Modifier.weight(0.2f)) // 빈 공간 추가
        }

        LazyColumn {
            items(beaconCounts) { beaconCount ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color.White)
                        .padding(0.3.dp)
                        .border(width = 1.dp, color = Color(0xFFF0F0F0))
                        .padding(0.3.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        beaconCount.position,
                        modifier = Modifier
                            .weight(1f)
                            .padding(14.dp)
                            .clickable {

                            },
                        style = MaterialTheme.typography.bodyMedium
                    )



                    Text(
                        "${beaconCount.counts}",
                        modifier = Modifier
                            .weight(1f)
                            .padding(14.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Button(
                        onClick = {
                            selectedPosition = beaconCount.position
                            showDialog = true
                        },
                        enabled = connection
                        ,
                        modifier = Modifier
                            .weight(0.2f) // 버튼도 1f의 비율로 너비 지정
                            .padding(2.dp) // 패딩을 줄여 버튼의 전체 크기 감소

                    ) {
                        Text("init", fontSize = 10.sp) // 텍스트 크기도 조정 가능
                    }

                }
            }
        }
    }

    if (showDialog && connection) {
        AlertDi(positionName = selectedPosition, watchSettingViewModel = watchSettingViewModel, {
            showDialog = false // 대화상자 닫기
        })
    }
}

@Composable
fun AlertDi(positionName: String, watchSettingViewModel: WatchSettingViewModel, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = {
            onDismiss()
        },
        title = {
            Text(
                text = "$positionName 위치의 비콘 스캔값이 초기화됩니다.",
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            Button(onClick = {
                watchSettingViewModel.deleteBeacon(PositionNameData(position = positionName))
                onDismiss()
            }) {
                Text("확인")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("취소")
            }
        }
    )
}
