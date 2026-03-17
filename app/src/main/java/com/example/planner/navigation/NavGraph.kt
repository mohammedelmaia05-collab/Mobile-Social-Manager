package com.example.planner.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.planner.screens.*
import com.example.planner.screens.screens.AIAssistantScreen
import com.example.planner.screens.screens.AIChatScreen

@Composable
fun SetupNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "welcome" // This sets the very first screen that loads
    ) {
        // Auth & Onboarding
        composable("welcome") { WelcomeScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }

        // Main Bottom Navigation Screens
        composable("calendar") { CalendarScreen(navController) }
        composable("analytics") { AnalyticsScreen(navController) }
        composable("community") { CommunityScreen(navController) }
        composable("profile") { ProfileScreen(navController) }

        // Core Features
        composable("create_post") { CreatePostScreen(navController) }
        composable("workflow") { WorkflowScreen(navController) }
        composable("post_details") { PostDetailsScreen(navController) }
        composable("notifications") { NotificationsScreen(navController) }

        // Settings & Profile Sub-menus
        composable("edit_profile") { EditProfileScreen(navController) }
        composable("change_password") { ChangePasswordScreen(navController) }
        composable("language") { LanguageScreen(navController) }
        composable("help") { HelpSupportScreen(navController) }

        // AI Tools
        composable("ai_assistant") { AIAssistantScreen(navController) }
        composable("ai_chat") { AIChatScreen(navController) }
    }
}