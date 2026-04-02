package com.example.planner.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ViewColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.planner.components.BottomNavBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun AnalyticsScreen(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid ?: ""

    // --- REAL DATA STATES ---
    var scheduledCount by remember { mutableIntStateOf(0) }
    var draftCount by remember { mutableIntStateOf(0) }
    var totalPublished by remember { mutableIntStateOf(0) }
    var platformStats by remember { mutableStateOf(mapOf<String, Int>()) }
    var isLoading by remember { mutableStateOf(true) }

    // Fetch data from Firestore
    LaunchedEffect(Unit) {
        if (userId.isNotEmpty()) {
            db.collection("posts")
                .whereEqualTo("userId", userId)
                .addSnapshotListener { snapshot, _ ->
                    if (snapshot != null) {
                        val docs = snapshot.documents
                        scheduledCount = docs.count { it.getString("status") == "ready" }
                        draftCount = docs.count { it.getString("status") == "draft" }
                        totalPublished = docs.size // For this example, total = all docs

                        // Calculate platform distribution
                        val distribution = mutableMapOf<String, Int>()
                        docs.forEach { doc ->
                            val platforms = doc.get("platforms") as? List<String> ?: emptyList()
                            platforms.forEach { platform ->
                                distribution[platform] = distribution.getOrDefault(platform, 0) + 1
                            }
                        }
                        platformStats = distribution
                        isLoading = false
                    }
                }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF002B36), Color(0xFF005F73))))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            Text("Analytics", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(20.dp))

            // Overview Grid with Real Data
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard("Posts Scheduled", scheduledCount.toString(), Modifier.weight(1f))
                StatCard("Draft Posts", draftCount.toString(), Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard("Approved Posts", scheduledCount.toString(), Modifier.weight(1f))
                StatCard("Total Managed", totalPublished.toString(), Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- CHART AREA ---
            Card(
                modifier = Modifier.fillMaxWidth().height(250.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Content Activity", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Last 7 Days", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(24.dp))

                    Canvas(modifier = Modifier.fillMaxSize().padding(bottom = 20.dp)) {
                        val width = size.width
                        val height = size.height

                        // Fixed data for now (Can be linked to creation dates later)
                        val dataPoints = listOf(0.2f, 0.5f, 0.4f, 0.8f, 0.3f, 0.6f, 0.5f)
                        val stepX = width / (dataPoints.size - 1)

                        val path = Path().apply {
                            dataPoints.forEachIndexed { index, value ->
                                val x = index * stepX
                                // FIXED: (1f - value) makes high values appear high on screen
                                val y = (1f - value) * height
                                if (index == 0) moveTo(x, y) else lineTo(x, y)
                            }
                        }

                        // Fill under the line
                        val fillPath = Path().apply {
                            addPath(path)
                            lineTo(width, height)
                            lineTo(0f, height)
                            close()
                        }
                        drawPath(fillPath, Brush.verticalGradient(listOf(Color(0xFF5AB9C1).copy(alpha = 0.3f), Color.Transparent)))

                        // Draw main line
                        drawPath(path, Color(0xFF5AB9C1), style = Stroke(width = 6f, cap = StrokeCap.Round, join = StrokeJoin.Round))

                        // Draw circles
                        dataPoints.forEachIndexed { index, value ->
                            drawCircle(Color.White, radius = 5f, center = Offset(index * stepX, (1f - value) * height))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Manage Workflow Button
            Button(
                onClick = { navController.navigate("workflow") },
                modifier = Modifier.fillMaxWidth().height(55.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5AB9C1))
            ) {
                Icon(Icons.Default.ViewColumn, contentDescription = null, tint = Color(0xFF002B36))
                Spacer(modifier = Modifier.width(10.dp))
                Text("Manage Full Workflow", color = Color(0xFF002B36), fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Real Platform Distribution
            Text("PLATFORMS DISTRIBUTION", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(12.dp))

            val totalPlatformCount = platformStats.values.sum().coerceAtLeast(1)

            listOf("Instagram", "Facebook", "LinkedIn", "TikTok").forEach { platform ->
                val count = platformStats[platform] ?: 0
                val progress = count.toFloat() / totalPlatformCount.toFloat()
                val percentText = "${(progress * 100).toInt()}%"
                PlatformBar(platform, progress, percentText)
            }

            Spacer(modifier = Modifier.height(100.dp))
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
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.12f))
    ) {
        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.Start) {
            Text(label, color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun PlatformBar(name: String, progress: Float, percent: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
        Text(name, color = Color.White, modifier = Modifier.width(85.dp), fontSize = 13.sp)
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.weight(1f).height(8.dp).clip(RoundedCornerShape(4.dp)),
            color = Color(0xFF5AB9C1),
            trackColor = Color.White.copy(alpha = 0.1f),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(percent, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}