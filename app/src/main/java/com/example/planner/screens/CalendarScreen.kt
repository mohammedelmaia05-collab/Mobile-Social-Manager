package com.example.planner.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.planner.R
import com.example.planner.components.BottomNavBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

@Composable
fun CalendarScreen(navController: NavController) {
    val db = remember { FirebaseFirestore.getInstance() }
    val auth = remember { FirebaseAuth.getInstance() }

    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var posts by remember { mutableStateOf(listOf<Map<String, Any>>()) }
    var isLoadingPosts by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            isLoadingPosts = false
            errorMessage = "Not logged in"
            return@LaunchedEffect
        }

        db.collection("posts")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    isLoadingPosts = false
                    errorMessage = "Failed to load posts: ${error.message}"
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val fetchedPosts = snapshot.documents.mapNotNull { doc ->
                        val data = doc.data?.toMutableMap() ?: return@mapNotNull null
                        data["id"] = doc.id
                        data
                    }
                    posts = fetchedPosts.sortedByDescending { post ->
                        post["createdAt"] as? Long ?: 0L
                    }
                    isLoadingPosts = false
                }
            }
    }

    val scheduledDays = remember(posts, currentMonth) {
        posts.mapNotNull { post ->
            val dateString = post["date"] as? String ?: return@mapNotNull null
            try {
                val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)
                val date = dateFormat.parse(dateString) ?: return@mapNotNull null
                val cal = Calendar.getInstance().apply { time = date }
                val postMonth = cal.get(Calendar.MONTH) + 1
                val postYear = cal.get(Calendar.YEAR)
                if (postMonth == currentMonth.monthValue && postYear == currentMonth.year) {
                    cal.get(Calendar.DAY_OF_MONTH)
                } else null
            } catch (e: Exception) {
                null
            }
        }.toSet()
    }

    // ✅ FIX: Moved OUTSIDE of LazyVerticalGrid items block
    val firstDayOfWeek = remember(currentMonth) {
        val cal = Calendar.getInstance()
        cal.set(currentMonth.year, currentMonth.monthValue - 1, 1)
        cal.get(Calendar.DAY_OF_WEEK) - 1
    }

    // ✅ FIX: Moved OUTSIDE of LazyVerticalGrid items block
    val todayDay = remember {
        Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    }
    val todayMonth = remember {
        Calendar.getInstance().get(Calendar.MONTH) + 1
    }
    val todayYear = remember {
        Calendar.getInstance().get(Calendar.YEAR)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF002B36), Color(0xFF005F73))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = null,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "PLANNER",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp
                    )
                }
                IconButton(onClick = { navController.navigate("ai_assistant") }) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color(0xFF5AB9C1)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Calendar Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.1f)
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {

                    // Month navigation row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = "${currentMonth.month.getDisplayName(
                                    TextStyle.FULL, Locale.ENGLISH
                                ).uppercase()} ${currentMonth.year}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            )
                            IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Row {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(20.dp)
                                    .clickable { navController.navigate("notifications") }
                            )
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(20.dp)
                                    .clickable { navController.navigate("profile") }
                            )
                        }
                    }

                    // Day headers row
                    val dayHeaders = listOf("S", "M", "T", "W", "T", "F", "S")
                    Row(modifier = Modifier.fillMaxWidth()) {
                        dayHeaders.forEach { day ->
                            Box(
                                modifier = Modifier.weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = day,
                                    color = Color.White.copy(alpha = 0.5f),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Calendar Grid
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(7),
                        modifier = Modifier
                            .height(240.dp)
                            .padding(top = 4.dp),
                        userScrollEnabled = false,
                        contentPadding = PaddingValues(4.dp)
                    ) {
                        // ✅ Empty offset cells before day 1
                        items(firstDayOfWeek) {
                            Box(modifier = Modifier.aspectRatio(1f))
                        }

                        // Day cells
                        items(currentMonth.lengthOfMonth()) { index ->
                            val dayNumber = index + 1
                            val hasPosts = scheduledDays.contains(dayNumber)

                            // ✅ FIX: No remember inside items — use plain val
                            val isToday = dayNumber == todayDay &&
                                    currentMonth.monthValue == todayMonth &&
                                    currentMonth.year == todayYear

                            Box(
                                modifier = Modifier
                                    .padding(3.dp)
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        when {
                                            hasPosts -> Color(0xFF5AB9C1).copy(alpha = 0.35f)
                                            isToday -> Color.White.copy(alpha = 0.15f)
                                            else -> Color.White.copy(alpha = 0.05f)
                                        }
                                    )
                                    .clickable {
                                        navController.navigate(
                                            "day_posts/${currentMonth.year}/${currentMonth.monthValue}/$dayNumber"
                                        )
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "$dayNumber",
                                        color = if (hasPosts) Color(0xFF5AB9C1) else Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = if (hasPosts || isToday) FontWeight.Bold else FontWeight.Normal
                                    )
                                    // Dot indicator for days with posts
                                    if (hasPosts) {
                                        Box(
                                            modifier = Modifier
                                                .size(4.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF5AB9C1))
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(25.dp))

            // Posts section header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ALL UPCOMING POSTS",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                IconButton(
                    onClick = { navController.navigate("create_post") },
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFF5AB9C1), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Color(0xFF002B36),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            // Posts list states
            when {
                isLoadingPosts -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF5AB9C1))
                    }
                }

                errorMessage != null -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = errorMessage ?: "Unknown error",
                            color = Color.Red.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                    }
                }

                posts.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.3f),
                                modifier = Modifier.size(60.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No posts yet",
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Tap + to create your first post",
                                color = Color.White.copy(alpha = 0.3f),
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 100.dp)
                    ) {
                        items(posts) { post ->
                            val postId = post["id"] as? String ?: ""
                            val dateStr = post["date"] as? String ?: "No Date"
                            val timeStr = post["time"] as? String ?: "No Time"
                            val caption = post["caption"] as? String ?: ""
                            val platformsList = post["platforms"] as? List<*>
                            val platform = platformsList?.firstOrNull()?.toString() ?: "Social"

                            val shortDate = try {
                                val fullDate = SimpleDateFormat(
                                    "MMM dd, yyyy", Locale.ENGLISH
                                ).parse(dateStr)
                                SimpleDateFormat(
                                    "MMM dd", Locale.ENGLISH
                                ).format(fullDate!!)
                            } catch (e: Exception) {
                                dateStr.take(6)
                            }

                            val iconRes = when (platform.lowercase()) {
                                "instagram" -> R.drawable.ic_instagram
                                "tiktok" -> R.drawable.ic_tiktok
                                "linkedin" -> R.drawable.ic_linkedin
                                else -> R.drawable.ic_facebook
                            }

                            ContentItem(
                                navController = navController,
                                postId = postId,
                                date = shortDate,
                                time = timeStr,
                                iconRes = iconRes,
                                platform = platform,
                                title = caption
                            )
                        }
                    }
                }
            }
        }

        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            BottomNavBar(navController)
        }
    }
}

@Composable
fun ContentItem(
    navController: NavController,
    postId: String,
    date: String,
    time: String,
    iconRes: Int,
    platform: String,
    title: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (postId.isNotEmpty()) navController.navigate("post_details/$postId")
            },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.08f)
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(70.dp)
            ) {
                Text(
                    text = date,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = time,
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 11.sp
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        Color.White.copy(alpha = 0.05f),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = platform,
                    color = Color(0xFF5AB9C1),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 14.sp,
                    maxLines = 1
                )
            }
        }
    }
}