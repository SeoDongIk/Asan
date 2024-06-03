package com.example.asan_service.feature

import android.graphics.Paint
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
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
        var selectedItem by remember { mutableStateOf<Item?>(null) }
        val scrollState = rememberScrollState()
        val usersState by viewModel.users.observeAsState(initial = emptyList())
        val connectedUsers = usersState.filter { it.connected }

        var items = connectedUsers.map {
            val currentTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(it.date), ZoneId.systemDefault())
            Item(it.name, it.host, it.watchId, currentTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
        }

        val heartRates by viewModel.heartRates.observeAsState(initial = emptyList())
        val accXs by viewModel.accXs.observeAsState(initial = emptyList())
        val accYs by viewModel.accYs.observeAsState(initial = emptyList())
        val accZs by viewModel.accZs.observeAsState(initial = emptyList())

        Log.d("ziped_data", "heartRates : " + heartRates.toString())

        var heartRates_first = if(heartRates.isNotEmpty()) heartRates[0].first else 0
        var heartRates_now = if(heartRates.isNotEmpty()) heartRates[0].second else 0
        var heartRates_ziped = if (heartRates.isNotEmpty()) {
            heartRates.filter { it.second.toInt() != 0 }
                .map {
                    Pair(((heartRates_first - it.first).toFloat()), it.second)
                }
        } else emptyList()

        Log.d("ziped_data", "heartRates_first : " + heartRates_first.toString())
        Log.d("ziped_data", "heartRates_ziped : " + heartRates_ziped.toString())


        var accSVM = sumOfSquareRoots(accXs.map { it.second.toInt() }, accYs.map { it.second.toInt() }, accZs.map { it.second.toInt() })
        var accSVM_time_first = if(accXs.isNotEmpty()) accXs[0].first else 0
        val accSVM_value_now = if (accXs.isNotEmpty() && accYs.isNotEmpty() && accZs.isNotEmpty()) {
            val sum = sqrt(
                accXs[0].second.toDouble() * accXs[0].second +
                        accYs[0].second.toDouble() * accYs[0].second +
                        accZs[0].second.toDouble() * accZs[0].second
            ).toFloat()
            sum
        } else {
            0f
        }
        var accSVM_time = accXs.map { (accSVM_time_first -it.first).toFloat() }
        var accSVM_ziped = accSVM_time.zip(accSVM)

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
                    .fillMaxWidth().height(30.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${selectedItem?.name}",
                    modifier = Modifier.weight(1f),

                )
                Text(
                    text = "watch Id : " +  selectedItem?.watchId ,
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
                    Text(
                        text = "HEART_RATE",
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = Color.Black
                    )

                    Text(
                        text = "$heartRates_now",
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = Color.Black
                    )

                    TimeSeriesGraph(heartRates_ziped)



                    Spacer(modifier = Modifier.height(16.dp))


                    Text(
                        text = "ACC_SVM",
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = Color.Black
                    )
                    Text(
                        text = "$accSVM_value_now",
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = Color.Black
                    )

                    TimeSeriesGraph2(accSVM_ziped)

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun TimeSeriesGraph(data: List<Pair<Float, Float>>) {
    val scrollState = rememberScrollState()
    Log.d("ziped_data", "heartRates_ziped : $data")

    Canvas(
        modifier = Modifier
            .padding(50.dp)
            .fillMaxWidth()
            .height(300.dp)
    ) {
        val paint = Paint().apply {
            color = Color.Black.toArgb()
            textSize = 30f
            textAlign = Paint.Align.CENTER
        }

        // Set fixed y-axis range
        val yMax = 160f
        val yMin = 0f

        // Determine step sizes for grid lines
        val yGridStep = size.height / 4
        val yLabelStep = 40f
        val xGridStep = size.width / 12

        // Draw horizontal grid lines and labels
        for (i in 0..4) {
            val y = size.height - (i * yGridStep)
            drawLine(
                start = Offset(0f, y),
                end = Offset(size.width, y),
                color = Color.LightGray,
                strokeWidth = 1f
            )
            drawIntoCanvas { canvas ->
                canvas.nativeCanvas.drawText(
                    "${(yMin + i * yLabelStep).toInt()}",
                    size.width + 25f,
                    y + 10f,
                    paint
                )
            }
        }

        // Draw vertical grid lines
        for (i in 1..12) {
            drawLine(
                start = Offset(i * xGridStep, 0f),
                end = Offset(i * xGridStep, size.height),
                color = Color.LightGray,
                strokeWidth = 1f
            )
        }

        // Draw x-axis
        drawLine(
            start = Offset(0f, size.height),
            end = Offset(size.width, size.height),
            color = Color.Red,
            strokeWidth = 4f
        )

        // Draw y-axis
        drawLine(
            start = Offset(size.width, 0f),
            end = Offset(size.width, size.height),
            color = Color.Red,
            strokeWidth = 4f
        )

        // Draw x-axis labels
        for (i in 1..12) {
            drawIntoCanvas { canvas ->
                canvas.nativeCanvas.drawText(
                    "${120 - i * 10}(s)",
                    i * xGridStep,
                    size.height + 35f,
                    paint
                )
            }
        }

        // Calculate step sizes for data points
        val xStep = size.width / 120f
        val yStep = size.height / (yMax - yMin)

        // Draw data points
        data.forEachIndexed { index, pair ->
            val x = pair.first * xStep
            val y = (pair.second - yMin) * yStep

            if (index == 0) {
                drawLine(
                    start = Offset(size.width, size.height - y),
                    end = Offset(size.width, size.height - y),
                    color = Color(0x04, 0x61, 0x66),
                    strokeWidth = 4f
                )
            } else {
                val prevPair = data[index - 1]
                val prevX = prevPair.first * xStep
                val prevY = (prevPair.second - yMin) * yStep

                drawLine(
                    start = Offset(size.width - prevX, size.height - prevY),
                    end = Offset(size.width - x, size.height - y),
                    color = Color(0x04, 0x61, 0x66),
                    strokeWidth = 4f
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

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun TimeSeriesGraph2(data: List<Pair<Float, Float>>) {
    val scrollState = rememberScrollState()
    Log.d("ziped_data", "accSVM_ziped : $data")

    Canvas(
        modifier = Modifier
            .padding(50.dp)
            .fillMaxWidth()
            .height(300.dp)
    ) {
        val paint = Paint().apply {
            color = Color.Black.toArgb()
            textSize = 30f
            textAlign = Paint.Align.CENTER
        }

        // Set fixed y-axis range
        val yMax = 80f
        val yMin = 0f

        // Determine step sizes for grid lines
        val yGridStep = size.height / 4
        val yLabelStep = 20f
        val xGridStep = size.width / 12

        // Draw horizontal grid lines and labels
        for (i in 0..4) {
            val y = size.height - (i * yGridStep)
            drawLine(
                start = Offset(0f, y),
                end = Offset(size.width, y),
                color = Color.LightGray,
                strokeWidth = 1f
            )
            drawIntoCanvas { canvas ->
                canvas.nativeCanvas.drawText(
                    "${(yMin + i * yLabelStep).toInt()}",
                    size.width + 25f,
                    y + 10f,
                    paint
                )
            }
        }

        // Draw vertical grid lines
        for (i in 1..12) {
            drawLine(
                start = Offset(i * xGridStep, 0f),
                end = Offset(i * xGridStep, size.height),
                color = Color.LightGray,
                strokeWidth = 1f
            )
        }

        // Draw x-axis
        drawLine(
            start = Offset(0f, size.height),
            end = Offset(size.width, size.height),
            color = Color.Red,
            strokeWidth = 4f
        )

        // Draw y-axis
        drawLine(
            start = Offset(size.width, 0f),
            end = Offset(size.width, size.height),
            color = Color.Red,
            strokeWidth = 4f
        )

        // Draw x-axis labels
        for (i in 1..12) {
            drawIntoCanvas { canvas ->
                canvas.nativeCanvas.drawText(
                    "${120 - i * 10}(s)",
                    i * xGridStep,
                    size.height + 35f,
                    paint
                )
            }
        }

        // Calculate step sizes for data points
        val xStep = size.width / 120f
        val yStep = size.height / (yMax - yMin)

        // Draw data points
        data.forEachIndexed { index, pair ->
            val x = pair.first * xStep
            val y = (pair.second - yMin) * yStep

            if (x <= size.width) {
                if (index == 0) {
                    drawLine(
                        start = Offset(size.width, size.height - y),
                        end = Offset(size.width, size.height - y),
                        color = Color(0x04, 0x61, 0x66),
                        strokeWidth = 4f
                    )
                } else {
                    val prevPair = data[index - 1]
                    val prevX = prevPair.first * xStep
                    val prevY = (prevPair.second - yMin) * yStep

                    drawLine(
                        start = Offset(size.width - prevX, size.height - prevY),
                        end = Offset(size.width - x, size.height - y),
                        color = Color(0x04, 0x61, 0x66),
                        strokeWidth = 4f
                    )
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
                                    .border(1.dp, Color.LightGray,RoundedCornerShape(8.dp))
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable {
                                        viewModel.changeValue(item.watchId)
                                        onItemSelected(item)
                                        expanded = false
                                        viewModel.insertSendState(item.watchId.toLong())
                                        viewModel.deleteAccs()
                                    }
                                    .padding(vertical = 8.dp, horizontal = 16.dp)
                            ) {
                                Text(text = "Name: ${item.name}")
//                                Text(text = "Room: ${item.room}")
                                Text(text = "Watch ID: ${item.watchId}")
                            }
                            Spacer(modifier = Modifier.height(4.dp))
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