package com.example.asan_service.feature


import android.content.Intent
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
import androidx.navigation.NavHostController
import com.example.asan_service.MyWebSocketService
import com.example.asan_service.PositionList
import com.example.asan_service.viewmodel.ImageViewModel
import com.example.asan_service.viewmodel.ScannerSettingViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchSettingScreen(navController: NavHostController, viewModel: ImageViewModel, scannerSettingViewModel: ScannerSettingViewModel) {
    val watchId = navController.currentBackStackEntry?.arguments?.getString("watchId")?.toLong()
    var collectedMinutes by remember { mutableStateOf(0) } // 수집된 시간(분)
    var collectedSeconds by remember { mutableStateOf(0) } // 수집된 시간(초)
    val positionList = viewModel.positionList.observeAsState().value
    var userInput by remember { mutableStateOf("") }
    var timerActive by remember { mutableStateOf(false) }
    var timerDurationSeconds by remember { mutableStateOf(0) }
    var timerRemainingSeconds by remember { mutableStateOf(0) }
    var showConfirmationDialog by remember { mutableStateOf(false) }
    val endTimes by viewModel.endTimes.collectAsState()
    var savedEndTime = endTimes[watchId]

    viewModel.getPositionList()


    LaunchedEffect(watchId) {
        watchId?.let {
            viewModel.getCollectionStatus(it.toString())
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
                }
            )
        }
    ) { innerPadding ->

        var selectedItem by remember { mutableStateOf<String?>(null) }
        var selectedImageId by remember { mutableStateOf<Long?>(null) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Scaffold로부터 제공받은 내부 패딩 적용
                .verticalScroll(rememberScrollState())
                .padding(vertical = 20.dp)

        ) {
            Row(
                modifier = Modifier
                    .background(Color.LightGray)
                    .fillMaxWidth(), // 텍스트의 배경색을 수평적으로 전체 영역에 적용
                verticalAlignment = Alignment.CenterVertically // 텍스트를 세로 중앙에 정렬
            ) {

                Text(
                    text = "사용자 지정", // 텍스트 내용
                    color = Color(0x04, 0x61, 0x66), // 텍스트 색상
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 3.dp) // 내부 패딩
                )

            }

            Spacer(modifier = Modifier.height(16.dp))

            BasicTextField(
                value = userInput, // 현재 상태 변수를 value로 바인딩
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
                        viewModel.changeName(watchId,userInput,"none")
                    }
                    scannerSettingViewModel.changeNickName(watchId.toString(), userInput)
                    showConfirmationDialog = true
                    context.startService(intent)
                }, // 버튼 클릭 시 수행할 동작을 정의합니다.
                colors = ButtonDefaults.buttonColors(
                    containerColor =  Color(0x04, 0x61, 0x66)
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
                selectedImageId = selectedImageId
            ) { name, imageId ->
                selectedItem = name
                selectedImageId = imageId }


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
                )}

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
                }
                    timerActive = false
                }


            val currentTime = System.currentTimeMillis()

            if (savedEndTime == null) {

                    if (!timerActive) {
                        TimePicker(onMinuteSelected = { minute ->
                            selectedMinute = minute * 60
                        })
                    } else {
                        TimerDisplay(timerRemainingSeconds)
                    }
            } else {
                timerActive = true
                val remainTimeMillis = savedEndTime!!.minus(currentTime)
                timerRemainingSeconds = (remainTimeMillis / 1000).toInt()
                TimerDisplay(timerRemainingSeconds)
            }


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        val endTime = System.currentTimeMillis() + (selectedMinute * 1000)
                        selectedImageId?.let {
                            viewModel.insertState(watchId.toString(),
                                it,selectedItem.toString(),endTime )
                        }
                        timerActive = true
                        collectedMinutes = 0 // 초기화
                        collectedSeconds = 0 // 초기화
                        // 수집 중인 시간을 업데이트하는 로직을 추가할 수 있습니다.
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor =  Color(0x04, 0x61, 0x66)
                    ),
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .weight(1f),
                    enabled = selectedItem != null
                ) {
                    Text(text = "비콘 수집")
                }
                Button(
                    onClick = {
                              timerActive = false
                        if (watchId != null) {
                            viewModel.deleteEndTime(watchId)
                        }
                              viewModel.deleteState(watchId.toString())}, // 버튼 클릭 시 수집 중단
                    colors = ButtonDefaults.buttonColors(
                        containerColor =  Color(0x04, 0x61, 0x66)
                    ),
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .weight(1f),
                ) {
                    Text(text = "수집 중지")
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
                .clickable { expanded = true }
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
            range = 0..59, // This defines the range of minutes that can be selected
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
fun TimerDisplay(seconds: Int) {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    Text(
        text = String.format("%02d:%02d", minutes, remainingSeconds),
        style = MaterialTheme.typography.displayLarge,
        modifier = Modifier.padding(16.dp)
    )
}