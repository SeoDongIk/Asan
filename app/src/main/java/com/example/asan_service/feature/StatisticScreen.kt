package com.example.asan_service.feature

import androidx.compose.foundation.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.asan_service.viewmodel.StaticalViewModel
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
        var accSVM = sumOfSquareRoots(accXs, accYs, accZs)
        val gyroXs by viewModel.gyroXs.observeAsState(initial = emptyList())
        val gyroYs by viewModel.gyroYs.observeAsState(initial = emptyList())
        val gyroZs by viewModel.gyroZs.observeAsState(initial = emptyList())
        val lights by viewModel.lights.observeAsState(initial = emptyList())
        val heartRates by viewModel.heartRates.observeAsState(initial = emptyList())
        val baros by viewModel.baros.observeAsState(initial = emptyList())

        var selectedItem by remember { mutableStateOf<Item?>(null) }
        val scrollState = rememberScrollState()

        val items = usersState.map {
            val currentTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(it.date), ZoneId.systemDefault())
            Item(it.name, it.host, it.watchId, currentTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
        }

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
                    HeartGraph(heartRates, "heartRates")
                    Spacer(modifier = Modifier.height(16.dp))
                    Graph(accSVM,"accSVM")
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
fun Graph(data: List<Int>, name : String) {
    val maxValue = data.maxOrNull() ?: 0
    val minValue = data.minOrNull() ?: 0
    val maxY = maxValue + 1
    val minY = minValue - 1

    Canvas(modifier = Modifier.fillMaxSize()) {
        val graphWidth = size.width
        val graphHeight = size.height

        val xStep = graphWidth / data.size.toFloat()
        val yStep = graphHeight / (maxY - minY).toFloat()

        val path = Path()
        path.moveTo(0f, graphHeight)

        for ((index, value) in data.withIndex()) {
            val x = index * xStep
            val y = graphHeight - (value - minY) * yStep
            path.lineTo(x, y)
        }

        path.lineTo(graphWidth, graphHeight)
        path.close()

        drawPath(
            path = path,
            color = Color.Blue,
            style = Stroke(width = 2f)
        )
    }
    Text(
        text = name,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )
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

fun sumOfSquareRoots(list1: List<Int>, list2: List<Int>, list3: List<Int>): List<Int> {
    require(list1.size == list2.size && list2.size == list3.size) { "Lists must have the same size" }

    val result = mutableListOf<Int>()
    for (i in list1.indices) {
        val sum = sqrt(list1[i].toDouble() * list1[i] + list2[i].toDouble() * list2[i] + list3[i].toDouble() * list3[i]).toInt()
        result.add(sum)
    }
    return result
}
