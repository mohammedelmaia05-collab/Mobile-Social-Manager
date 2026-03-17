package com.example.planner.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.planner.components.BottomNavBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(navController: NavController) {
    var caption by remember { mutableStateOf("") }
    var isReadyForReview by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF002B36), Color(0xFF005F73))))) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBackIos, contentDescription = null, tint = Color.White)
                }
                Text("Create Post", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Upload Media Box (Dashed style)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f))
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color(0xFF5AB9C1), modifier = Modifier.size(48.dp))
                    Text("Drop your image here or browse", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Caption Field
            Text("Caption", color = Color.White, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
            OutlinedTextField(
                value = caption,
                onValueChange = { caption = it },
                placeholder = { Text("The start of a wonderful story...", color = Color.White.copy(alpha = 0.3f)) },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
                    focusedContainerColor = Color.White.copy(alpha = 0.15f)
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Platform Chips (Mockup)
            Text("Platforms", color = Color.White, fontSize = 14.sp)
            Row(modifier = Modifier.padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PlatformChip("Instagram", true)
                PlatformChip("LinkedIn", false)
                PlatformChip("TikTok", false)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Schedule Row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ScheduleField("Date", Icons.Default.CalendarMonth, Modifier.weight(1f))
                ScheduleField("Time", Icons.Default.KeyboardArrowDown, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Status Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Status", color = Color.White, fontWeight = FontWeight.Bold)
                Switch(
                    checked = isReadyForReview,
                    onCheckedChange = { isReadyForReview = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF5AB9C1))
                )
                Text(if (isReadyForReview) "Ready for Review" else "Draft", color = Color.White)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { /* Save logic */ },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF005F73)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save Post", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
        Box(modifier = Modifier.align(Alignment.BottomCenter)) { BottomNavBar(navController) }
    }
}

@Composable
fun PlatformChip(name: String, selected: Boolean) {
    Surface(
        color = if (selected) Color(0xFF5AB9C1).copy(alpha = 0.3f) else Color.White.copy(alpha = 0.1f),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, if (selected) Color(0xFF5AB9C1) else Color.Transparent)
    ) {
        Text(name, color = Color.White, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontSize = 12.sp)
    }
}

@Composable
fun ScheduleField(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier) {
    OutlinedTextField(
        value = "",
        onValueChange = {},
        label = { Text(label, color = Color.White.copy(alpha = 0.5f)) },
        trailingIcon = { Icon(icon, contentDescription = null, tint = Color.White) },
        modifier = modifier,
        readOnly = true,
        shape = RoundedCornerShape(12.dp)
    )
}
