package com.example.planner.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF002B36), Color(0xFF005F73))))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Header Section
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = null,
                    modifier = Modifier.size(50.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "SOCIAL MEDIA\nPLANNER & CALENDAR",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(25.dp))

            // Calendar Glass Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.12f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Month Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row {
                            Text("<", color = Color.White, fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(">", color = Color.White, fontSize = 20.sp)
                        }
                        Text("MARCH 2026", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Row {
                            Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.Default.Settings, contentDescription = null, tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(15.dp))

                    // Simplified Calendar Grid
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(7),
                        modifier = Modifier.height(220.dp),
                        userScrollEnabled = false
                    ) {
                        items(31) { index ->
                            Box(
                                modifier = Modifier
                                    .padding(2.dp)
                                    .aspectRatio(1f)
                                    .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("${index + 1}", color = Color.White, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(25.dp))

            // Today's Content Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("TODAY'S CONTENT", color = Color.White, fontWeight = FontWeight.Bold)
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0xFF003366), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            // Content List
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item { ContentItem("March 23", "8:00 AM", R.drawable.logo, "Instagram post:", "\"Summer launch!\" [Ready]") }
                item { ContentItem("March 25", "11:30 AM", R.drawable.logo, "TikTok Video:", "\"Behind the Scenes\" [Scheduled]") }
            }
        }

        // Fixed Bottom Nav Bar
        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            BottomNavBar(navController)
        }
    }
}

@Composable
fun ContentItem(date: String, time: String, iconRes: Int, platform: String, title: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(date, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(time, color = Color.White, fontSize = 10.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Box(modifier = Modifier.size(40.dp).background(Color.White, RoundedCornerShape(8.dp))) {
                Image(painter = painterResource(id = iconRes), contentDescription = null, modifier = Modifier.padding(4.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(platform, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text(title, color = Color.LightGray, fontSize = 11.sp)
            }
        }
    }
}