package com.example.asan_service

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.asan_service.core.AppDatabase
import com.example.asan_service.feature.MoniteringScreen
import com.example.asan_service.feature.StatisticScreen
import com.example.asan_service.feature.WatchSettingScreen
import com.example.asan_service.ui.theme.Asan_ServiceTheme
import com.example.asan_service.viewmodel.ConnectScreenViewModel
import com.example.asan_service.viewmodel.ImageViewModel
import com.example.asan_service.viewmodel.PasswordViewModel
import com.example.asan_service.viewmodel.ScannerSettingViewModel
import com.example.asan_service.viewmodel.StaticalViewModel
import com.example.asan_service.viewmodel.WatchSettingViewModel



class MainActivity : ComponentActivity() {
    private lateinit var db: AppDatabase
    private val MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    private lateinit var viewModel: ImageViewModel
    //    private lateinit var watchSettingViewModel: WatchSettingViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = AppDatabase.getInstance(applicationContext)
        startService(Intent(this, MyWebSocketService::class.java))
        viewModel = ViewModelProvider(this)[ImageViewModel::class.java]
//        watchSettingViewModel = ViewModelProvider(this)[WatchSettingViewModel::class.java]
        val passwordViewModel: PasswordViewModel = PasswordViewModel()


        WatchSettingViewModel(db.watchItemDao()).getCountBeacon()
        viewModel.getImageList()
        viewModel.getPositionList()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // 권한이 승인되지 않았다면 요청합니다
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // 권한이 승인되지 않았다면 요청합니다
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }




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
                            MainScreen(navController,viewModel,passwordViewModel)
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
                                db.heartRateDao())
                            )
                        }
                        composable("BackgroundSettingScreen") {
                            BackgroundSettingScreen(navController, viewModel,passwordViewModel)
                        }
                        composable("BackgroundDetailScreen/{imageId}?imageName={imageName}") { backStackEntry ->
                            BackgroundDetailScreen(navController, viewModel,passwordViewModel)
                        }
                        composable("ScannerSettingScreen") {
                            ScannerSettingScreen(navController,
                                ConnectScreenViewModel(db.watchItemDao()),passwordViewModel)
                        }
                        composable("WatchSettingScreen/{watchId}/{watchName}/{connected}") {
                            WatchSettingScreen(navController,viewModel,
                                ConnectScreenViewModel(db.watchItemDao()),WatchSettingViewModel(db.watchItemDao())
                            )
                        }
                        composable("MoniteringScreen/{imageId}?imageName={imageName}") {
                            MoniteringScreen(navController, viewModel , passwordViewModel)
                        }

                    }
                }
            }
        }
    }



    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // 권한이 승인되었습니다. 위치 관련 작업을 계속할 수 있습니다.
            } else {
                // 권한이 거부되었습니다. 사용자에게 권한이 필요한 이유를 설명하거나
                // 권한이 없을 때의 대체 작업을 처리하세요.
            }
        }
    }
}