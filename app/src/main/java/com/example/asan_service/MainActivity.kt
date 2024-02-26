package com.example.asan_service

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.asan_service.core.AppDatabase
import com.example.asan_service.feature.StatisticScreen
import com.example.asan_service.feature.WatchSettingScreen
import com.example.asan_service.ui.theme.Asan_ServiceTheme
import com.example.asan_service.viewmodel.ConnectScreenViewModel
import com.example.asan_service.viewmodel.StaticalViewModel

class MainActivity : ComponentActivity() {
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = AppDatabase.getInstance(applicationContext)
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
                            ConnectScreen(navController, ConnectScreenViewModel(db.watchItemDao()))
                        }
                        composable("AlarmScreen") {
                            AlaramScreen(navController)
                        }
                        composable("StatisticScreen") {
                            StatisticScreen(navController, StaticalViewModel(db.watchItemDao(),
                            db.accXDao(),
                            db.accYDao(),
                            db.accZDao(),
                            db.gyroXDao(),
                            db.gyroYDao(),
                            db.gyroZDao(),
                            db.lightDao(),
                            db.heartRateDao(),
                            db.baroDao())
                            )
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

