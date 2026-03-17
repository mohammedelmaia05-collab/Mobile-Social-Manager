package com.example.planner.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.planner.components.BottomNavBar

@Composable
fun AnalyticsScreen(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF002B36), Color(0xFF005F73))))) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text("Analytics", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(16.dp))

            // Overview Grid
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard("Posts Scheduled", "23", Modifier.weight(1f))
                StatCard("Draft Posts", "8", Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard("Approved Posts", "15", Modifier.weight(1f))
                StatCard("Total Published", "108", Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Performance Card (Placeholder for Chart)
            Card(
                modifier = Modifier.fillMaxWidth().height(200.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Content Activity", color = Color.White, fontWeight = FontWeight.Bold)
                    // You can drop a Canvas here for the line chart later
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("[Chart Area]", color = Color.White.copy(alpha = 0.5f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Platform Distribution
            Text("PLATFORMS Distribution", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(10.dp))
            PlatformBar("Instagram", 0.4f, "40%")
            PlatformBar("LinkedIn", 0.3f, "30%")
            PlatformBar("TikTok", 0.2f, "20%")
        }

        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            BottomNavBar(navController)
        }
    }
}

@Composable
fun StatCard(label: String, value: String, modifier: Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
    ) {
        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, color = Color.White, fontSize = 12.sp)
            Text(value, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun PlatformBar(name: String, progress: Float, percent: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
        Text(name, color = Color.White, modifier = Modifier.width(80.dp), fontSize = 12.sp)
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.weight(1f).height(8.dp),
            color = Color(0xFF5AB9C1),
            trackColor = Color.White.copy(alpha = 0.2f),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(percent, color = Color.White, fontSize = 12.sp)
    }
}
