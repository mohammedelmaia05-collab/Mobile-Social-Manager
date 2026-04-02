package com.example.planner.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
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
import com.example.planner.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DayPostsScreen(navController: NavController, year: Int, month: Int, day: Int) {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    var posts by remember { mutableStateOf(listOf<Map<String, Any>>()) }
    var isLoading by remember { mutableStateOf(true) }

    // Convert the year, month, day back into the "MMM dd, yyyy" format used in Firebase
    val targetDateString = remember(year, month, day) {
        val cal = Calendar.getInstance()
        cal.set(year, month - 1, day) // Month is 0-indexed in Calendar
        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(cal.time)
    }

    // Fetch posts for this specific date
    LaunchedEffect(targetDateString) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("posts")
                .whereEqualTo("userId", userId)
                .whereEqualTo("date", targetDateString) // Filter exact day
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, _ ->
                    if (snapshot != null) {
                        posts = snapshot.documents.map { doc ->
                            val data = doc.data?.toMutableMap() ?: mutableMapOf()
                            data["id"] = doc.id
                            data
                        }
                    }
                    isLoading = false
                }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF002B36), Color(0xFF005F73))))) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
            Spacer(modifier = Modifier.height(40.dp))

            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBackIos, contentDescription = "Back", tint = Color.White)
                }
                Text("Posts for $targetDateString", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF5AB9C1))
                }
            } else if (posts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No posts scheduled for this day.", color = Color.White.copy(alpha = 0.6f))
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(posts) { post ->
                        val postId = post["id"] as? String ?: ""
                        val dateStr = post["date"] as? String ?: "No Date"
                        val timeStr = post["time"] as? String ?: "No Time"
                        val caption = post["caption"] as? String ?: ""
                        val platformsList = post["platforms"] as? List<*>
                        val platform = platformsList?.firstOrNull()?.toString() ?: "Social"

                        val shortDate = try {
                            val fullDate = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).parse(dateStr)
                            SimpleDateFormat("MMM dd", Locale.getDefault()).format(fullDate!!)
                        } catch (e: Exception) { dateStr.take(6) }

                        val iconRes = when(platform.lowercase()) {
                            "instagram" -> R.drawable.ic_instagram
                            "tiktok" -> R.drawable.ic_tiktok
                            "linkedin" -> R.drawable.ic_linkedin
                            else -> R.drawable.ic_facebook
                        }

                        ContentItem(navController, postId, shortDate, timeStr, iconRes, platform, caption)
                    }
                }
            }
        }
    }
}