package com.kutumb.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.kutumb.app.ui.navigation.KutumbApp
import com.kutumb.app.ui.theme.KutumbTheme
import com.kutumb.app.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MainViewModel = hiltViewModel()
            val settings by viewModel.settingsState.collectAsState()
            KutumbTheme(
                darkTheme = settings.isDarkMode,
                seedTheme = settings.seedTheme
            ) {
                KutumbApp(viewModel = viewModel)
            }
        }
    }
}
