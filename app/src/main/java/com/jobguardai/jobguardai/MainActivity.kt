package com.jobguardai.jobguardai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.jobguardai.jobguardai.ui.navigation.AppNavigation
import com.jobguardai.jobguardai.ui.theme.JobguardAiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JobguardAiTheme {
                AppNavigation()
            }
        }
    }
}
