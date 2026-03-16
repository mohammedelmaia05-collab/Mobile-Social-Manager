package com.example.planner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.*
import com.example.planner.screens.CalendarScreen
import com.example.planner.screens.RegisterScreen
import com.example.planner.screens.LoginScreen
import com.example.planner.screens.WelcomeScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = "welcome"
            ) {

                composable("welcome") {
                    WelcomeScreen(navController)
                }

                composable("login") {
                    LoginScreen(navController)
                }

                composable("register") {
                    RegisterScreen(navController)
                }
                composable("calendar") { CalendarScreen(navController) }
            }
        }
    }
}