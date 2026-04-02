package com.example.planner.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.planner.R
import com.example.planner.components.BottomNavBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// Helper function to map platform names to your drawable icons
@Composable
fun getPlatformIcon(platform: String): Int {
    return when (platform.lowercase()) {
        "instagram" -> R.drawable.ic_instagram
        "tiktok" -> R.drawable.ic_tiktok
        "linkedin" -> R.drawable.ic_linkedin
        "facebook" -> R.drawable.ic_facebook
        else -> R.drawable.logo // Default fallback
    }
}

@Composable
fun WorkflowScreen(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid ?: ""

    // Lists to hold posts from Firebase
    var draftPosts by remember { mutableStateOf(listOf<Map<String, Any>>()) }
    var readyPosts by remember { mutableStateOf(listOf<Map<String, Any>>()) }
    var approvedPosts by remember { mutableStateOf(listOf<Map<String, Any>>()) }

    // Real-time listener for Firebase data
    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            db.collection("posts")
                .whereEqualTo("userId", userId)
                .addSnapshotListener { snapshot, _ ->
                    if (snapshot != null) {
                        // FIX: We must capture the document ID to allow navigation
                        val allPosts = snapshot.documents.map { doc ->
                            val data = doc.data?.toMutableMap() ?: mutableMapOf()
                            data["id"] = doc.id
                            data
                        }
                        draftPosts = allPosts.filter { it["status"] == "draft" }
                        readyPosts = allPosts.filter { it["status"] == "ready" }
                        approvedPosts = allPosts.filter { it["status"] == "approved" }
                    }
                }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF002B36), Color(0xFF005F73))))) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBackIos, contentDescription = null, tint = Color.White)
                }
                Text("Workflow", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                // --- DRAFTS ---
                if (draftPosts.isNotEmpty()) {
                    item { WorkflowSectionHeader("DRAFTS (${draftPosts.size})") }
                    items(draftPosts) { post ->
                        val pList = post["platforms"] as? List<*>
                        val platformList = pList?.filterIsInstance<String>() ?: listOf("Unknown")
                        val postId = post["id"] as? String ?: ""

                        WorkflowItem(
                            platform = platformList.firstOrNull() ?: "App",
                            date = post["date"]?.toString() ?: "No date",
                            snippet = post["caption"]?.toString() ?: "No caption",
                            onClick = {
                                if (postId.isNotEmpty()) navController.navigate("post_details/$postId")
                            }
                        )
                    }
                }

                // --- READY FOR REVIEW ---
                if (readyPosts.isNotEmpty()) {
                    item { WorkflowSectionHeader("READY FOR REVIEW (${readyPosts.size})") }
                    items(readyPosts) { post ->
                        val pList = post["platforms"] as? List<*>
                        val platformList = pList?.filterIsInstance<String>() ?: listOf("Unknown")
                        val postId = post["id"] as? String ?: ""

                        WorkflowItem(
                            platform = platformList.firstOrNull() ?: "App",
                            date = post["date"]?.toString() ?: "No date",
                            snippet = post["caption"]?.toString() ?: "No caption",
                            onClick = {
                                if (postId.isNotEmpty()) navController.navigate("post_details/$postId")
                            }
                        )
                    }
                }

                // --- APPROVED ---
                if (approvedPosts.isNotEmpty()) {
                    item { WorkflowSectionHeader("APPROVED (${approvedPosts.size})") }
                    items(approvedPosts) { post ->
                        val pList = post["platforms"] as? List<*>
                        val platformList = pList?.filterIsInstance<String>() ?: listOf("Unknown")
                        val postId = post["id"] as? String ?: ""

                        WorkflowItem(
                            platform = platformList.firstOrNull() ?: "App",
                            date = post["date"]?.toString() ?: "No date",
                            snippet = post["caption"]?.toString() ?: "No caption",
                            onClick = {
                                if (postId.isNotEmpty()) navController.navigate("post_details/$postId")
                            }
                        )
                    }
                }
            }
        }
        Box(modifier = Modifier.align(Alignment.BottomCenter)) { BottomNavBar(navController) }
    }
}

@Composable
fun WorkflowSectionHeader(title: String) {
    Text(
        text = title,
        color = Color(0xFF5AB9C1),
        fontSize = 14.sp,
        fontWeight = FontWeight.ExtraBold,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    )
}

// FIX: Added the onClick parameter and Modifier.clickable
@Composable
fun WorkflowItem(platform: String, date: String, snippet: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }, // Makes the card tappable
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {

            // --- PLATFORM ICON ---
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = getPlatformIcon(platform)),
                    contentDescription = platform,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(platform, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.weight(1f))
                    Text(date, color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                }
                Text(
                    text = snippet,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}