package com.example.planner.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.planner.screens.*
import com.example.planner.screens.screens.AIAssistantScreen
import com.example.planner.screens.screens.AIChatScreen

@Composable
fun SetupNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "welcome"
    ) {
        // Core Screens
        composable("welcome") { WelcomeScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("calendar") { CalendarScreen(navController) }
        composable("workflow") { WorkflowScreen(navController) }
        composable("analytics") { AnalyticsScreen(navController) }
        composable("community") { CommunityScreen(navController) }
        composable("profile") { ProfileScreen(navController) }
        composable("notifications") { NotificationsScreen(navController) }

        // Profile Sub-Screens
        composable("edit_profile") { EditProfileScreen(navController) }
        composable("change_password") { ChangePasswordScreen(navController) }
        composable("language") { LanguageScreen(navController) }
        composable("help") { HelpSupportScreen(navController) }

        // Create Post Route - Fixed to match the function signature
        composable(
            route = "create_post?postId={postId}",
            arguments = listOf(
                navArgument("postId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId")
            CreatePostScreen(navController, postId)
        }

        // Post Details Route
        composable(
            route = "post_details/{postId}",
            arguments = listOf(navArgument("postId") { type = NavType.StringType })
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId") ?: ""
            PostDetailsScreen(navController, postId)
        }

        composable("ai_assistant") { AIAssistantScreen(navController) }
        composable("ai_chat") { AIChatScreen(navController) }
        // 1. Add this for the new Day Posts screen
        composable(
            route = "day_posts/{year}/{month}/{day}",
            arguments = listOf(
                navArgument("year") { type = NavType.IntType },
                navArgument("month") { type = NavType.IntType },
                navArgument("day") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val year = backStackEntry.arguments?.getInt("year") ?: 2026
            val month = backStackEntry.arguments?.getInt("month") ?: 1
            val day = backStackEntry.arguments?.getInt("day") ?: 1
            DayPostsScreen(navController, year, month, day)
        }

// 2. Add this for your Notifications screen (if you have created the file for it)
// composable("notifications") {
//     NotificationsScreen(navController)
// }
    }
}