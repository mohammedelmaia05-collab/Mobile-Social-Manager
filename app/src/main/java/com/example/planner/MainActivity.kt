package com.example.planner

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.cloudinary.android.MediaManager
import com.example.planner.navigation.SetupNavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Initialize Cloudinary BEFORE setting the content
        initializeCloudinary()

        setContent {
            // 2. Now start the UI
            val navController = rememberNavController()
            SetupNavGraph(navController = navController)
        }
    }

    private fun initializeCloudinary() {
        try {
            val config = hashMapOf(
                "cloud_name" to "df843wl3s", // Your Cloud Name
                "secure" to true
            )
            MediaManager.init(this, config)
        } catch (e: Exception) {
            // This prevents the app from crashing if it's already initialized
            Log.d("Cloudinary", "Cloudinary already initialized or error: ${e.message}")
        }
    }
}