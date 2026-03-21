package com.example.planner.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.planner.components.BottomNavBar
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(navController: NavController) {
    val context = LocalContext.current
    // Use full path to avoid conflicts with other 'Calendar' objects
    val calendar = java.util.Calendar.getInstance()

    var captionText by remember { mutableStateOf("") }
    var hashtagText by remember { mutableStateOf("") }
    var selectedPlatforms by remember { mutableStateOf(setOf<String>()) }
    var scheduledDateText by remember { mutableStateOf("") }
    var scheduledTimeText by remember { mutableStateOf("") }
    var isReadyForReview by remember { mutableStateOf(false) }

    var showTimePicker by remember { mutableStateOf(false) }

    val timePickerState = rememberTimePickerState(
        initialHour = calendar.get(java.util.Calendar.HOUR_OF_DAY),
        initialMinute = calendar.get(java.util.Calendar.MINUTE),
        is24Hour = false
    )

    val platforms = listOf("Instagram", "Facebook", "LinkedIn", "TikTok")

    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF002B36), Color(0xFF005F73))))) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBackIos, contentDescription = null, tint = Color.White)
                }
                Text("Create Post", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Media Area
            Card(
                modifier = Modifier.fillMaxWidth().height(160.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f))
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color(0xFF5AB9C1).copy(alpha = 0.7f), modifier = Modifier.size(40.dp))
                    Text("Upload image or video", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Caption Section
            Text("Caption", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = captionText,
                onValueChange = { captionText = it },
                placeholder = { Text("Write your caption here...", color = Color.White.copy(alpha = 0.3f)) },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF5AB9C1),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f)
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Hashtags Section
            Text("Hashtags", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = hashtagText,
                onValueChange = { hashtagText = it },
                placeholder = { Text("#marketing #digital", color = Color.White.copy(alpha = 0.3f)) },
                leadingIcon = { Icon(Icons.Default.Tag, contentDescription = null, tint = Color(0xFF5AB9C1)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF5AB9C1),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f)
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Platforms
            Text("Platforms", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                platforms.forEach { platform ->
                    val isSelected = selectedPlatforms.contains(platform)
                    FilterChip(
                        selected = isSelected, // Explicitly passed to fix compiler error
                        onClick = {
                            selectedPlatforms = if (isSelected) selectedPlatforms - platform else selectedPlatforms + platform
                        },
                        label = { Text(platform, fontSize = 12.sp) },
                        enabled = true, // Explicitly passed to fix compiler error
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF5AB9C1),
                            selectedLabelColor = Color(0xFF002B36),
                            containerColor = Color.White.copy(alpha = 0.1f),
                            labelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Schedule
            Text("Schedule", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = scheduledDateText,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("Date", color = Color.White.copy(alpha = 0.4f)) },
                    trailingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = Color.White) },
                    modifier = Modifier.weight(1f).clickable {
                        DatePickerDialog(context, { _, y, m, d ->
                            calendar.set(y, m, d)
                            scheduledDateText = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(calendar.time)
                        }, calendar.get(java.util.Calendar.YEAR), calendar.get(java.util.Calendar.MONTH), calendar.get(java.util.Calendar.DAY_OF_MONTH)).show()
                    },
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(disabledBorderColor = Color.White.copy(alpha = 0.2f), disabledTextColor = Color.White)
                )

                OutlinedTextField(
                    value = scheduledTimeText,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("Time", color = Color.White.copy(alpha = 0.4f)) },
                    trailingIcon = { Icon(Icons.Default.AccessTime, contentDescription = null, tint = Color.White) },
                    modifier = Modifier.weight(1f).clickable { showTimePicker = true },
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(disabledBorderColor = Color.White.copy(alpha = 0.2f), disabledTextColor = Color.White)
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Status Section
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Status", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(
                        text = if (isReadyForReview) "Ready for Review" else "Draft",
                        color = Color(0xFF5AB9C1),
                        fontSize = 12.sp
                    )
                }
                Switch(
                    checked = isReadyForReview,
                    onCheckedChange = { isReadyForReview = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF002B36),
                        checkedTrackColor = Color(0xFF5AB9C1)
                    )
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5AB9C1)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save Post", color = Color(0xFF002B36), fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(100.dp))
        }

        if (showTimePicker) {
            AlertDialog(
                onDismissRequest = { showTimePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        val cal = java.util.Calendar.getInstance()
                        cal.set(java.util.Calendar.HOUR_OF_DAY, timePickerState.hour)
                        cal.set(java.util.Calendar.MINUTE, timePickerState.minute)
                        scheduledTimeText = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(cal.time)
                        showTimePicker = false
                    }) { Text("OK", color = Color(0xFF5AB9C1)) }
                },
                text = { TimePicker(state = timePickerState) }
            )
        }

        Box(modifier = Modifier.align(Alignment.BottomCenter)) { BottomNavBar(navController) }
    }
}