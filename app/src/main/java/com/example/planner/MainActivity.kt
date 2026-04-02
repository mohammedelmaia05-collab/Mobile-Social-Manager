package com.example.planner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.cloudinary.android.MediaManager
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
        // Inside onCreate
        val config = mapOf(
            "cloud_name" to "df843wl3s" // Your Cloud Name from screenshot
        )
        try {
            val config = hashMapOf(
                "cloud_name" to "df843wl3s", // Replace with your actual Cloudinary cloud name
                "secure" to true
            )
            MediaManager.init(this, config)
        } catch (e: Exception) {
            // This catches the error if it's already initialized
        }

    }
}