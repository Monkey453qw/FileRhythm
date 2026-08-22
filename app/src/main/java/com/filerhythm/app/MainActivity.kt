package com.filerhythm.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.filerhythm.app.ui.FileRhythmApp
import com.filerhythm.app.ui.theme.FileRhythmTheme
import com.filerhythm.app.ui.theme.SettingsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.factory(this))
            val settings by settingsViewModel.settings.collectAsState()

            FileRhythmTheme(
                themeMode = settings.themeMode,
                dynamicColor = settings.dynamicColor
            ) {
                FileRhythmApp()
            }
        }
    }
}
