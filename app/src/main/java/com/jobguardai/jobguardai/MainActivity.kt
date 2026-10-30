package com.jobguardai.jobguardai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.jobguardai.jobguardai.data.remote.RetrofitClient
import com.jobguardai.jobguardai.ui.navigation.AppNavigation
import com.jobguardai.jobguardai.ui.theme.JobguardAiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RetrofitClient.initialize(applicationContext)
        enableEdgeToEdge()
        setContent {
            JobguardAiTheme {
                AppNavigation()
            }
        }
    }
}
