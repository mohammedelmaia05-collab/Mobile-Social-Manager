package com.example.planner.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.planner.R
import com.example.planner.components.BottomNavBar

@Composable
fun ProfileScreen(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF002B36), Color(0xFF005F73))))) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBackIos, contentDescription = null, tint = Color.White)
                }
                Text("Profile", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // User Profile Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.12f))
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(modifier = Modifier.size(100.dp).background(Color.Gray, CircleShape)) {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Mohammed Elamaia", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Digital Developer", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Settings Sections
            Text("Account Settings", color = Color(0xFF5AB9C1), fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))

            SettingsRow(Icons.Default.Person, "Edit Profile") { navController.navigate("edit_profile") }
            SettingsRow(Icons.Default.Lock, "Change Password") { navController.navigate("change_password") }
            SettingsRow(Icons.Default.Notifications, "Notifications") { navController.navigate("notifications") }

            Spacer(modifier = Modifier.height(20.dp))

            Text("App Settings", color = Color(0xFF5AB9C1), fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))

            SettingsRow(Icons.Default.DarkMode, "Dark Mode") { /* Toggle Logic */ }
            SettingsRow(Icons.Default.Language, "Language") { navController.navigate("language") }
            SettingsRow(Icons.Default.Info, "Help & Support") { navController.navigate("help") }

            Spacer(modifier = Modifier.weight(1f))

            // Logout Button - Fixed logic to clear backstack
            Button(
                onClick = {
                    navController.navigate("login") {
                        popUpTo("welcome") { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB00020)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Logout", color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(80.dp)) // Nav bar space
        }
        Box(modifier = Modifier.align(Alignment.BottomCenter)) { BottomNavBar(navController) }
    }
}

@Composable
fun SettingsRow(icon: ImageVector, title: String, onClick: () -> Unit) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(title, color = Color.White, modifier = Modifier.weight(1f), fontSize = 15.sp)
            Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = Color.White.copy(alpha = 0.4f), modifier = Modifier.size(14.dp))
        }
        HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
    }
}
