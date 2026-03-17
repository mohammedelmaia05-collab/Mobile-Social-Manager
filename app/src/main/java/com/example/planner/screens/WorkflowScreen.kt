package com.example.planner.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
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
fun WorkflowScreen(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF002B36), Color(0xFF005F73))))) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBackIos, contentDescription = null, tint = Color.White)
                }
                Text("Workflow", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                item { WorkflowSectionHeader("DRAFTS (2)") }
                items(2) { WorkflowItem("Instagram", "Mar 23", "[New product launch...]") }

                item { WorkflowSectionHeader("READY FOR REVIEW (1)") }
                items(1) { WorkflowItem("TikTok", "Mar 23", "[Behind the scenes...]") }

                item { WorkflowSectionHeader("APPROVED (1)") }
                items(1) { WorkflowItem("LinkedIn", "Mar 24", "[Our journey story...]") }

                item { Spacer(modifier = Modifier.height(100.dp)) }
            }
        }
        Box(modifier = Modifier.align(Alignment.BottomCenter)) { BottomNavBar(navController) }
    }
}

@Composable
fun WorkflowSectionHeader(title: String) {
    Text(title, color = Color(0xFF5AB9C1), fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
}

@Composable
fun WorkflowItem(platform: String, date: String, snippet: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            // Placeholder for Thumbnail
            Box(modifier = Modifier.size(50.dp).background(Color.Gray, RoundedCornerShape(8.dp)))

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(platform, color = Color.White, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.weight(1f))
                    Text(date, color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                }
                Text(snippet, color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
            }
        }
    }
}
