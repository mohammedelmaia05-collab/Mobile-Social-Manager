package com.example.planner.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.planner.R
import com.example.planner.components.BottomNavBar

@Composable
fun CalendarScreen(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF002B36), Color(0xFF005F73))))) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            Spacer(modifier = Modifier.height(40.dp))

            // Header Section
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(painter = painterResource(id = R.drawable.logo), contentDescription = null, modifier = Modifier.size(40.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("PLANNER", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                }
                IconButton(onClick = { navController.navigate("ai_assistant") }) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF5AB9C1))
                }
            }

            Spacer(modifier = Modifier.height(25.dp))

            // Calendar Glass Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.12f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("MARCH 2026", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Row {
                            IconButton(onClick = { navController.navigate("notifications") }) {
                                Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.White)
                            }
                            IconButton(onClick = { navController.navigate("profile") }) {
                                Icon(Icons.Default.Settings, contentDescription = null, tint = Color.White)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(7),
                        modifier = Modifier.height(200.dp),
                        userScrollEnabled = false
                    ) {
                        items(31) { index ->
                            Box(
                                modifier = Modifier
                                    .padding(2.dp)
                                    .aspectRatio(1f)
                                    .background(if (index == 22) Color(0xFF5AB9C1) else Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("${index + 1}", color = if (index == 22) Color.Black else Color.White, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(25.dp))

            // Today's Content Header
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("TODAY'S CONTENT", color = Color.White, fontWeight = FontWeight.Bold)
                IconButton(
                    onClick = { navController.navigate("create_post") },
                    modifier = Modifier.size(32.dp).background(Color(0xFF5AB9C1), CircleShape)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF002B36), modifier = Modifier.size(20.dp))
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            // Content List
            LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item { ContentItem(navController, "March 23", "8:00 AM", R.drawable.logo, "Instagram", "Summer launch!") }
                item { ContentItem(navController, "March 25", "11:30 AM", R.drawable.logo, "TikTok", "Behind the Scenes") }
            }
            Spacer(modifier = Modifier.height(80.dp))
        }

        Box(modifier = Modifier.align(Alignment.BottomCenter)) { BottomNavBar(navController) }
    }
}

@Composable
fun ContentItem(navController: NavController, date: String, time: String, iconRes: Int, platform: String, title: String) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { navController.navigate("post_details") },
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(date, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(time, color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Box(modifier = Modifier.size(40.dp).background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                Image(painter = painterResource(id = iconRes), contentDescription = null, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(platform, color = Color(0xFF5AB9C1), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text(title, color = Color.White, fontSize = 13.sp)
            }
        }
    }
}