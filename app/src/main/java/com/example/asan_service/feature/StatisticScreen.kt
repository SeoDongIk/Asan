package com.example.asan_service.feature

import android.graphics.Paint
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.asan_service.viewmodel.StaticalViewModel
import kotlinx.coroutines.delay
import java.lang.Math.sqrt
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticScreen(navController : NavController, viewModel: StaticalViewModel) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0x04, 0x61, 0x66),
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                title = {
                    Text(
                        "통계량",
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
        val usersState by viewModel.users.observeAsState(initial = emptyList())
        val accXs by viewModel.accXs.observeAsState(initial = emptyList())
        val accYs by viewModel.accYs.observeAsState(initial = emptyList())
        val accZs by viewModel.accZs.observeAsState(initial = emptyList())
        var accSVM = sumOfSquareRoots(accXs.map { it.second.toInt() }, accYs.map { it.second.toInt() }, accZs.map { it.second.toInt() })
        var accSVM_time = accXs.map { it.first }
        val heartRates by viewModel.heartRates.observeAsState(initial = emptyList())

        var selectedItem by remember { mutableStateOf<Item?>(null) }
        val scrollState = rememberScrollState()

        val items = usersState.map {
            val currentTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(it.date), ZoneId.systemDefault())
            Item(it.name, it.host, it.watchId, currentTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
        }

        Log.d("dfdf", "test : " + accSVM.toString())

        val data2 = accSVM_time.zip(accSVM)

        val data = listOf(
            Pair(0f, 20f),
            Pair(0.5f, 20f),
            Pair(1f, 20f),
            Pair(1.5f, 20f),
            Pair(2f, 20f),
            Pair(3f, 20f),
            Pair(4f, 20f),
            Pair(5f, 20f),
            Pair(6f, 20f),
            Pair(7f, 20f),
            Pair(8f, 20f),
            Pair(9f, 20f),
            Pair(10f, 45f),
            Pair(11f, 50f),
            Pair(12f, 30f),
            Pair(13f, 55f),
            Pair(14f, 60f),
            Pair(15f, 40f),
            Pair(16f, 4f),
            Pair(17f, 40f),
            Pair(18f, 30f),
            Pair(19f, 20f),
            Pair(20f, 20f),
            Pair(21f, 60f),
            Pair(22f, 10f),
            Pair(23f, 30f),
            Pair(24f, 40f),
            Pair(25f, 20f),
            Pair(26f, 10f),
            Pair(27f, 20f),
            Pair(28f, 20f),
            Pair(29f, 50f),
            Pair(30f, 50f),
            Pair(31f, 30f),
            Pair(32f, 40f),
            Pair(33f, 20f),
            Pair(34f, 30f),
            Pair(35f, 10f),
            Pair(36f, 40f),
            Pair(37f, 30f),
            Pair(38f, 20f),
            Pair(39f, 60f),
            Pair(40f, 40f),
            Pair(41f, 60f),
            Pair(42f, 30f),
            Pair(43f, 20f),
            Pair(44f, 20f),
            Pair(45f, 10f),
            Pair(46f, 50f),
            Pair(47f, 20f),
            Pair(48f, 60f),
            Pair(49f, 20f),
            Pair(51f, 10f),
            Pair(52f, 20f),
            Pair(53f, 15f),
            Pair(54f, 25f),
            Pair(55f, 30f),
            Pair(56f, 20f),
            Pair(57f, 35f),
            Pair(58f, 40f),
            Pair(59f, 25f),
            Pair(60f, 45f),
            Pair(61f, 50f),
            Pair(72f, 30f),
            Pair(73f, 55f),
            Pair(74f, 60f),
            Pair(75f, 40f),
            Pair(76f, 4f),
            Pair(77f, 40f),
            Pair(78f, 30f),
            Pair(79f, 20f),
            Pair(80f, 20f),
            Pair(91f, 60f),
            Pair(92f, 10f),
            Pair(93f, 30f),
            Pair(94f, 40f),
            Pair(95f, 20f),
            Pair(96f, 10f),
            Pair(97f, 20f),
            Pair(98f, 20f),
            Pair(99f, 50f),
            Pair(130f, 50f),
            Pair(131f, 30f),
            Pair(132f, 40f),
            Pair(133f, 20f),
            Pair(134f, 30f),
            Pair(135f, 10f),
            Pair(136f, 40f),
            Pair(137f, 30f),
            Pair(138f, 20f),
            Pair(139f, 60f),
            Pair(140f, 140f),
            Pair(141f, 60f),
            Pair(142f, 30f),
            Pair(143f, 20f),
            Pair(144f, 20f),
            Pair(145f, 10f),
            Pair(146f, 50f),
            Pair(147f, 20f),
            Pair(148f, 60f),
            Pair(149f, 20f),
            Pair(150f, 40f),
            Pair(151f, 40f),
            Pair(152f, 40f),
            Pair(153f, 40f),
            Pair(154f, 40f),
            Pair(155f, 40f),
            Pair(156f, 40f),
            Pair(157f, 40f),
            Pair(158f, 40f),
            Pair(159f, 0f),
            )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            DropdownLayout(
                items = items,
                selectedItem = selectedItem,
                onItemSelected = { selectedItem = it },
                viewModel = viewModel
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

            Surface(
                modifier = Modifier
                    .fillMaxSize(),
                color = Color(0xFF, 0x57, 0xC1, 0x14)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    TimeSeriesGraph(heartRates)
                    Text(
                        text = "HEART_RATE",
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    TimeSeriesGraph2(data2)
                    Text(
                        text = "ACC_SVM",
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

data class Item(
    val name: String,
    val room: String,
    val watchId: String,
    val measuredPeriod: String
)

@Composable
fun DropdownLayout(
    items: List<Item>,
    selectedItem: Item?,
    onItemSelected: (Item) -> Unit,
    viewModel: StaticalViewModel
) {
    var expanded by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
    ) {
        Box(
            modifier = Modifier
                .background(Color.LightGray)
                .fillMaxWidth()
                .height(56.dp)
                .clickable { expanded = true },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "환자 선택",
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
        }

        if (expanded) {
            AlertDialog(
                onDismissRequest = { expanded = false },
                title = { Text("환자를 선택해주세요") },
                text = {
                    Column(
                        modifier = Modifier.verticalScroll(scrollState)
                    ) {
                        items.forEach { item ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.changeValue(item.watchId)
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
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun TimeSeriesGraph(data: List<Pair<Float, Float>>) {
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .horizontalScroll(scrollState)
            .padding(16.dp)
    ) {
        Canvas(
            modifier = Modifier
                .size(500.dp, 200.dp)
        ) {
            val paint = Paint().apply {
                color = Color.Black.toArgb()
                textSize = 30f
                textAlign = Paint.Align.CENTER
            }
            // x축 그리기
            drawLine(
                start = Offset(0f, size.height),
                end = Offset(size.width, size.height),
                color = Color.Red,
                strokeWidth = 2f
            )

            // y축 그리기
            drawLine(
                start = Offset(size.width, 0f),
                end = Offset(size.width, size.height),
                color = Color.Red,
                strokeWidth = 2f
            )

            // x축 눈금 그리기
            val xStep2 = size.width / 12
            for (i in 1..12) {
                drawLine(
                    start = Offset(i * xStep2, size.height - 5),
                    end = Offset(i * xStep2, size.height + 5),
                    color = Color.Black,
                    strokeWidth = 2f
                )
                drawIntoCanvas { canvas ->
                    canvas.nativeCanvas.drawText(
                        "${12-i}",
                        i * xStep2,
                        size.height + 35f,
                        paint
                    )
                }
            }

            // y축 눈금 그리기
            val yStep2 = size.height / 5
            for (i in 1..5) {
                drawLine(
                    start = Offset(size.width-5f, size.height - i * yStep2),
                    end = Offset(size.width+5f, size.height - i * yStep2),
                    color = Color.Black,
                    strokeWidth = 2f
                )
                drawIntoCanvas { canvas ->
                    canvas.nativeCanvas.drawText(
                        "${5*i}",
                        size.width + 25f,
                        size.height - i * yStep2 + 10f,
                        paint
                    )
                }
            }

            val xStep = size.width / 120f
            val yStep = size.height / 60f

            data.forEachIndexed { index, pair ->
                val x = pair.first * xStep
                val y = pair.second * yStep

                if (index == 0) {
                    drawLine(
                        start = Offset(size.width, size.height - y),
                        end = Offset(size.width, size.height - y),
                        color = Color(0x04, 0x61, 0x66),
                        strokeWidth = 2f
                    )
                } else {
                    val prevPair = data[index - 1]
                    val prevX = prevPair.first * xStep
                    val prevY = prevPair.second * yStep

                    drawLine(
                        start = Offset(size.width - prevX, size.height - prevY),
                        end = Offset(size.width - x, size.height - y),
                        color =Color(0x04, 0x61, 0x66),
                        strokeWidth = 2f
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
            ) {
                data.forEachIndexed { index, pair ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                    ) {
                        Text(
                            text = pair.first.toString(),
                            modifier = Modifier.align(Alignment.Center),
                            color = Color.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
            ) {
                data.forEachIndexed { index, pair ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                    ) {
                        Text(
                            text = pair.second.toString(),
                            modifier = Modifier.align(Alignment.Center),
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun TimeSeriesGraph2(data: List<Pair<Float, Float>>) {
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .horizontalScroll(scrollState)
            .padding(16.dp)
    ) {
        Canvas(
            modifier = Modifier
                .size(500.dp, 200.dp)
        ) {
            val paint = Paint().apply {
                color = androidx.compose.ui.graphics.Color.Black.toArgb()
                textSize = 30f
                textAlign = Paint.Align.CENTER
            }
            // x축 그리기
            drawLine(
                start = Offset(0f, size.height),
                end = Offset(size.width, size.height),
                color = Color.Red,
                strokeWidth = 2f
            )

            // y축 그리기
            drawLine(
                start = Offset(size.width, 0f),
                end = Offset(size.width, size.height),
                color = Color.Red,
                strokeWidth = 2f
            )

            // x축 눈금 그리기
            val xStep2 = size.width / 12
            for (i in 1..12) {
                drawLine(
                    start = Offset(i * xStep2, size.height - 5),
                    end = Offset(i * xStep2, size.height + 5),
                    color = Color.Black,
                    strokeWidth = 2f
                )
                drawIntoCanvas { canvas ->
                    canvas.nativeCanvas.drawText(
                        "${12-i}",
                        i * xStep2,
                        size.height + 35f,
                        paint
                    )
                }
            }

            // y축 눈금 그리기
            val yStep2 = size.height / 5
            for (i in 1..5) {
                drawLine(
                    start = Offset(size.width-5f, size.height - i * yStep2),
                    end = Offset(size.width+5f, size.height - i * yStep2),
                    color = Color.Black,
                    strokeWidth = 2f
                )
                drawIntoCanvas { canvas ->
                    canvas.nativeCanvas.drawText(
                        "${i*5}",
                        size.width + 25f,
                        size.height - i * yStep2 + 10f,
                        paint
                    )
                }
            }

            val xStep = size.width / 120f
            val yStep = size.height / 60f

            data.forEachIndexed { index, pair ->
                val x = pair.first * xStep
                val y = pair.second * yStep

                if (index == 0) {
                    drawLine(
                        start = Offset(size.width, size.height - y),
                        end = Offset(size.width, size.height - y),
                        color = Color(0x04, 0x61, 0x66),
                        strokeWidth = 2f
                    )
                } else {
                    val prevPair = data[index - 1]
                    val prevX = prevPair.first * xStep
                    val prevY = prevPair.second * yStep

                    drawLine(
                        start = Offset(size.width - prevX, size.height - prevY),
                        end = Offset(size.width - x, size.height - y),
                        color =Color(0x04, 0x61, 0x66),
                        strokeWidth = 2f
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
            ) {
                data.forEachIndexed { index, pair ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                    ) {
                        Text(
                            text = pair.first.toString(),
                            modifier = Modifier.align(Alignment.Center),
                            color = Color.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
            ) {
                data.forEachIndexed { index, pair ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                    ) {
                        Text(
                            text = pair.second.toString(),
                            modifier = Modifier.align(Alignment.Center),
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HeartGraph(data: List<Int>, text : String) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
    ) {
        val boxWidth = 300.dp.toPx()
        val boxHeight = 100.dp.toPx()
        val barSpacing = 5.dp.toPx()

        val maxBarHeight = boxHeight - 2 * barSpacing
        val barWidth = if (data.isNotEmpty()) (boxWidth - (data.size - 1) * barSpacing) / data.size else 0f

        data.forEachIndexed { index, value ->
            val x = index * (barWidth + barSpacing)
            val barHeight = (value / 120f) * maxBarHeight
            val y = boxHeight - barHeight

            drawRect(
                color = when {
                    value >= 150 -> Color.Red
                    else -> Color.Blue
                },
                topLeft = Offset(x, y),
                size = androidx.compose.ui.geometry.Size(barWidth, barHeight),
                style = Stroke(width = barWidth)
            )
        }
    }
    Text(
        text = if (data.isNotEmpty()) "심박수 ${data.last()}" else "심박수 없음",
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )
}

fun sumOfSquareRoots(list1: List<Int>, list2: List<Int>, list3: List<Int>): List<Float> {
    val nonEmptyLists = listOf(list1, list2, list3).filter { it.isNotEmpty() }
    if (nonEmptyLists.size < 3) {
        return emptyList()
    }

    val minSize = nonEmptyLists.minOf { it.size }

    val result = mutableListOf<Int>()
    for (i in 0 until minSize) {
        val sum = sqrt(
            nonEmptyLists[0].getOrElse(i) { 0 }.toDouble() * nonEmptyLists[0].getOrElse(i) { 0 } +
                    nonEmptyLists[1].getOrElse(i) { 0 }.toDouble() * nonEmptyLists[1].getOrElse(i) { 0 } +
                    nonEmptyLists[2].getOrElse(i) { 0 }.toDouble() * nonEmptyLists[2].getOrElse(i) { 0 }
        ).toInt()
        result.add(sum)
    }
    return result.map { it.toFloat() }
}