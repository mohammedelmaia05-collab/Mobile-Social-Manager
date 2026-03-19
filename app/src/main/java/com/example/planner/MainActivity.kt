package com.example.planner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.planner.navigation.SetupNavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // We only need to define the controller once
            val navController = rememberNavController()

            // We call our separate SetupNavGraph file to handle everything
            SetupNavGraph(navController = navController)
        }
    }
}