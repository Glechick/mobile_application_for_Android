package com.example.android_application

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.android_application.ui.navigation.QuestNavHost
import com.example.android_application.ui.theme.Android_ApplicationTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Android_ApplicationTheme {
                val navController = rememberNavController()
                QuestNavHost(navController)
            }
        }
    }
}