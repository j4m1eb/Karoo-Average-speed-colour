package com.j4m1eb.averagespeedcolour

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.j4m1eb.averagespeedcolour.screens.MainScreen
import com.j4m1eb.averagespeedcolour.screens.WelcomeScreen
import com.j4m1eb.averagespeedcolour.theme.AppTheme
import timber.log.Timber

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Timber.d("Timber initialized in Debug mode.")
        } else {
            Timber.d("Timber initialized in Release mode (no DebugTree planted).")
        }
        setContent {
            AppTheme {
                var showWelcome by remember { mutableStateOf(true) }

                if (showWelcome) {
                    WelcomeScreen(onGetStarted = { showWelcome = false })
                } else {
                    MainScreen()
                }
            }
        }
    }
}
