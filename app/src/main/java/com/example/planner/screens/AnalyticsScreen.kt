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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

@Composable
fun AnalyticsScreen(navController: NavController) {
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

            // --- REAL CHART AREA ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Content Activity", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Last 7 Days", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)

                    Spacer(modifier = Modifier.height(24.dp))

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val width = size.width
                        val height = size.height

                        // Data points for the line (0.0 to 1.0 scale)
                        val dataPoints = listOf(0.8f, 0.4f, 0.5f, 0.1f, 0.3f, 0.2f, 0.4f)
                        val stepX = width / (dataPoints.size - 1)

                        // 1. Draw the Path
                        val path = Path().apply {
                            dataPoints.forEachIndexed { index, value ->
                                val x = index * stepX
                                val y = value * height
                                if (index == 0) moveTo(x, y) else lineTo(x, y)
                            }
                        }

                        // 2. Draw a shadow/gradient area under the line
                        val fillPath = Path().apply {
                            addPath(path)
                            lineTo(width, height)
                            lineTo(0f, height)
                            close()
                        }
                        drawPath(
                            path = fillPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFF5AB9C1).copy(alpha = 0.3f), Color.Transparent)
                            )
                        )

                        // 3. Draw the main teal line
                        drawPath(
                            path = path,
                            color = Color(0xFF5AB9C1),
                            style = Stroke(
                                width = 8f,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )

                        // 4. Draw data point circles
                        dataPoints.forEachIndexed { index, value ->
                            drawCircle(
                                color = Color.White,
                                radius = 6f,
                                center = Offset(index * stepX, value * height)
                            )
                        }
                    }
                }
            }
            // --- END CHART AREA ---

            Spacer(modifier = Modifier.height(24.dp))

            // --- NEW: MANAGE WORKFLOW BUTTON ---
            Button(
                onClick = { navController.navigate("workflow") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5AB9C1))
            ) {
                Icon(
                    imageVector = Icons.Default.ViewColumn,
                    contentDescription = null,
                    tint = Color(0xFF002B36)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    "Manage Full Workflow",
                    color = Color(0xFF002B36),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Platform Distribution
            Text("PLATFORMS DISTRIBUTION", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(12.dp))
            PlatformBar("Instagram", 0.4f, "40%")
            PlatformBar("LinkedIn", 0.3f, "30%")
            PlatformBar("TikTok", 0.2f, "20%")

            Spacer(modifier = Modifier.height(100.dp)) // Space for BottomNavBar
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
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 6.dp)) {
        Text(name, color = Color.White, modifier = Modifier.width(85.dp), fontSize = 13.sp)
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .background(Color.Transparent, RoundedCornerShape(4.dp)),
            color = Color(0xFF5AB9C1),
            trackColor = Color.White.copy(alpha = 0.1f),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(percent, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}