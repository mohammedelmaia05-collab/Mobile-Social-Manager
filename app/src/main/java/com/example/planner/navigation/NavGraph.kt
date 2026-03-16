package com.example.planner.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.planner.screens.*

@Composable
fun SetupNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "welcome"
    ) {
        composable("welcome") { WelcomeScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("calendar") { CalendarScreen(navController) }
        composable("analytics") { AnalyticsScreen(navController) }
        composable("community") { CommunityScreen(navController) }
        composable("profile") { ProfileScreen(navController) }
        composable("edit_profile") { EditProfileScreen(navController) }
        composable("create_post") { CreatePostScreen(navController) }
        composable("workflow") { WorkflowScreen(navController) }
        composable("help") { HelpSupportScreen(navController) }
    }
}