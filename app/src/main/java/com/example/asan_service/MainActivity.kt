package com.example.asan_service

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.asan_service.feature.StatisticScreen
import com.example.asan_service.feature.WatchSettingScreen
import com.example.asan_service.ui.theme.Asan_ServiceTheme
import dagger.hilt.android.AndroidEntryPoint

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startService(Intent(this, MyWebSocketService::class.java))

        setContent {
            val navController = rememberNavController()
            Asan_ServiceTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = "MainScreen"
                    ) {
                        composable("MainScreen") {
                            MainScreen(navController)
                        }
                        composable("ConnectScreen") {
                            ConnectScreen(navController)
                        }
                        composable("AlarmScreen") {
                            AlaramScreen(navController)
                        }
                        composable("StatisticScreen") {
                            StatisticScreen(navController)
                        }
                        composable("BackgroundSettingScreen") {
                            BackgroundSettingScreen(navController)
                        }
                        composable("WatchSettingScreen") {
                            WatchSettingScreen(navController, "dfdf")
                        }
                    }
                }
            }
        }
    }
}

