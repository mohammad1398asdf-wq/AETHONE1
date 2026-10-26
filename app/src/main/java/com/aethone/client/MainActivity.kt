package com.aethone.client

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.graphics.Color as AndroidColor
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.SideEffect
import androidx.compose.foundation.isSystemInDarkTheme
import com.aethone.client.ui.AppViewModel
import com.aethone.client.ui.navigation.AethoneNavHost
import com.aethone.client.ui.theme.AethoneTheme
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : AppCompatActivity() {

    private val notifPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Own the system-bar insets explicitly (required for consistent layouts on Android 15+).
        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            notifPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        setContent {
            val vm: AppViewModel = viewModel()
            val settings by vm.settings.collectAsState()
            val systemDark = isSystemInDarkTheme()
            val darkUi = when (settings.themeMode) {
                com.aethone.client.data.ThemeMode.DARK -> true
                com.aethone.client.data.ThemeMode.LIGHT -> false
                com.aethone.client.data.ThemeMode.SYSTEM -> systemDark
            }
            SideEffect {
                val barColor = if (darkUi) AndroidColor.rgb(18, 20, 24) else AndroidColor.rgb(244, 247, 245)
                window.statusBarColor = barColor
                window.navigationBarColor = barColor
                val bars = WindowInsetsControllerCompat(window, window.decorView)
                bars.isAppearanceLightStatusBars = !darkUi
                bars.isAppearanceLightNavigationBars = !darkUi
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    window.isNavigationBarContrastEnforced = false
                    window.isStatusBarContrastEnforced = false
                }
            }
            AethoneTheme(themeMode = settings.themeMode) {
                AethoneNavHost()
            }
        }
    }
}
