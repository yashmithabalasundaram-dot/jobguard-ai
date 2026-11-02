package com.jobguardai.jobguardai

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.jobguardai.jobguardai.ui.navigation.AppNavigation
import com.jobguardai.jobguardai.ui.theme.JobguardAiTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContent {
            JobguardAiTheme {
                AppNavigation()
            }
        }
    }
}
