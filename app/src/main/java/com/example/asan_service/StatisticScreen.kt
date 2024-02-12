package com.example.asan_service

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
        DropdownExample()

    }
}

@Composable
fun DropdownOption(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.LightGray)
            .clickable { expanded = !expanded }
    ) {
        Text(
            text = selectedOption,
            modifier = Modifier
                .padding(16.dp)
        )
    }

    if (expanded) {
        DropdownList(
            options = options,
            selectedOption = selectedOption,
            onOptionSelected = { option ->
                onOptionSelected(option)
                expanded = false
            }
        )
    }
}

@Composable
fun DropdownList(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .width(IntrinsicSize.Max)
            .heightIn(max = 200.dp)
    ) {
        items(options) { option ->
            Text(
                text = option,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(if (option == selectedOption) Color.LightGray else Color.Transparent)
                    .clickable { onOptionSelected(option) }
            )
        }
    }
}

@Composable
fun DropdownExample() {
    var selectedOption by remember { mutableStateOf("Option 1") }

    val options = listOf("Option 1", "Option 2", "Option 3", "Option 4")

    DropdownOption(
        options = options,
        selectedOption = selectedOption,
        onOptionSelected = { option ->
            selectedOption = option
        }
    )
}