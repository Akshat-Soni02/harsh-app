package com.heroroutine.app

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.heroroutine.app.ui.champion.ChampionScreen
import com.heroroutine.app.ui.guardian.GuardianScreen
import com.heroroutine.app.ui.theme.HeroRoutineTheme
import com.heroroutine.app.viewmodel.AppMode
import com.heroroutine.app.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HeroRoutineTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val permissionsToRequest = remember {
                        val list = mutableListOf(
                            Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.CAMERA
                        )
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            list.add(Manifest.permission.POST_NOTIFICATIONS)
                            list.add(Manifest.permission.READ_MEDIA_IMAGES)
                        } else {
                            list.add(Manifest.permission.READ_EXTERNAL_STORAGE)
                        }
                        list.toTypedArray()
                    }

                    val permissionLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestMultiplePermissions()
                    ) { _ -> }

                    LaunchedEffect(Unit) {
                        permissionLauncher.launch(permissionsToRequest)
                    }

                    val mode by viewModel.currentMode.collectAsState()

                    Crossfade(targetState = mode, label = "ModeCrossfade") { currentMode ->
                        when (currentMode) {
                            AppMode.CHAMPION -> ChampionScreen(viewModel = viewModel)
                            AppMode.GUARDIAN -> GuardianScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}
