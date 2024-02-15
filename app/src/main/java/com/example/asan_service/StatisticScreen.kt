package com.example.asan_service

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticScreen(navController : NavController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                title = {
                    Text(
                        "통계량"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .border(width = 1.dp, color = Color.Blue) // 테두리 추가
        ) {
            DropdownLayout(
                items = items,
                selectedItem = selectedItem,
                onItemSelected = { selectedItem = it }
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .border(width = 1.dp, color = Color.Blue),
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
    onItemSelected: (Item) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .background(Color.Blue)
                .fillMaxWidth()
                .height(56.dp)
                .clickable { expanded = true },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "환자 선택",
                color = Color.White,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Dropdown Arrow",
                tint = Color.White,
                modifier = Modifier.size(48.dp)
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