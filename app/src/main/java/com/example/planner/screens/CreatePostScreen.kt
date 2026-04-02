package com.example.planner.screens

import android.app.DatePickerDialog
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.planner.components.BottomNavBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
// ✅ ADD THESE TWO IMPORTS FOR MAIN THREAD FIX
import android.os.Handler
import android.os.Looper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(navController: NavController, postId: String? = null) {
    val context = LocalContext.current
    val auth = remember { FirebaseAuth.getInstance() }
    val db = remember { FirebaseFirestore.getInstance() }
    val calendar = remember { Calendar.getInstance() }

    // ✅ Create a main thread handler once
    val mainHandler = remember { Handler(Looper.getMainLooper()) }

    var captionText by remember { mutableStateOf("") }
    var hashtagText by remember { mutableStateOf("") }
    var selectedPlatforms by remember { mutableStateOf(setOf<String>()) }
    var scheduledDateText by remember { mutableStateOf("") }
    var scheduledTimeText by remember { mutableStateOf("") }
    var isReadyForReview by remember { mutableStateOf(false) }
    var mediaUri by remember { mutableStateOf<Uri?>(null) }
    var remoteMediaUrl by remember { mutableStateOf<String?>(null) }
    var showTimePicker by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(postId != null) }

    val platformsList = listOf("Instagram", "Facebook", "LinkedIn", "TikTok")
    val timePickerState = rememberTimePickerState(
        initialHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
        initialMinute = Calendar.getInstance().get(Calendar.MINUTE)
    )

    LaunchedEffect(postId) {
        if (postId != null) {
            db.collection("posts").document(postId).get()
                .addOnSuccessListener { doc ->
                    if (doc != null && doc.exists()) {
                        captionText = doc.getString("caption") ?: ""
                        hashtagText = doc.getString("hashtags") ?: ""
                        scheduledDateText = doc.getString("date") ?: ""
                        scheduledTimeText = doc.getString("time") ?: ""
                        isReadyForReview = doc.getString("status") == "ready"
                        remoteMediaUrl = doc.getString("imageUrl")
                        val platforms = doc.get("platforms") as? List<*>
                        selectedPlatforms = platforms
                            ?.filterIsInstance<String>()
                            ?.toSet() ?: emptySet()
                    }
                    isLoading = false
                }
                .addOnFailureListener {
                    isLoading = false
                    Toast.makeText(context, "Failed to load post", Toast.LENGTH_SHORT).show()
                }
        }
    }

    val mediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            mediaUri = uri
        }
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
        if (isLoading) {
            CircularProgressIndicator(
                color = Color(0xFF5AB9C1),
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    Text(
                        text = if (postId == null) "Create Post" else "Edit Post",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { mediaLauncher.launch("image/*") },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.08f)
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            mediaUri != null -> {
                                AsyncImage(
                                    model = mediaUri,
                                    contentDescription = "Selected Image",
                                    contentScale = ContentScale.FillWidth,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentHeight()
                                        .clip(RoundedCornerShape(20.dp))
                                )
                            }
                            !remoteMediaUrl.isNullOrEmpty() -> {
                                AsyncImage(
                                    model = remoteMediaUrl,
                                    contentDescription = "Post Image",
                                    contentScale = ContentScale.FillWidth,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentHeight()
                                        .clip(RoundedCornerShape(20.dp))
                                )
                            }
                            else -> {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(40.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CloudUpload,
                                        contentDescription = "Upload",
                                        tint = Color(0xFF5AB9C1),
                                        modifier = Modifier.size(40.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Upload Image",
                                        color = Color.White.copy(alpha = 0.6f)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Caption",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = captionText,
                    onValueChange = { captionText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF5AB9C1),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        cursorColor = Color(0xFF5AB9C1)
                    ),
                    placeholder = {
                        Text(
                            text = "Write your caption here...",
                            color = Color.White.copy(alpha = 0.4f)
                        )
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Hashtags",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = hashtagText,
                    onValueChange = { hashtagText = it },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Tag,
                            contentDescription = null,
                            tint = Color(0xFF5AB9C1)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF5AB9C1),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        cursorColor = Color(0xFF5AB9C1)
                    ),
                    placeholder = {
                        Text(
                            text = "#hashtag",
                            color = Color.White.copy(alpha = 0.4f)
                        )
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Platforms",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    platformsList.forEach { platform ->
                        val isSelected = selectedPlatforms.contains(platform)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedPlatforms = if (isSelected) {
                                    selectedPlatforms - platform
                                } else {
                                    selectedPlatforms + platform
                                }
                            },
                            label = {
                                Text(
                                    text = platform,
                                    fontSize = 12.sp
                                )
                            },
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

                Text(
                    text = "Schedule",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = scheduledDateText,
                        onValueChange = {},
                        readOnly = true,
                        enabled = false,
                        placeholder = {
                            Text(
                                text = "Date",
                                color = Color.White.copy(alpha = 0.4f)
                            )
                        },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = "Pick Date",
                                tint = Color(0xFF5AB9C1),
                                modifier = Modifier.clickable {
                                    DatePickerDialog(
                                        context,
                                        { _, year, month, dayOfMonth ->
                                            calendar.set(year, month, dayOfMonth)
                                            scheduledDateText = SimpleDateFormat(
                                                "MMM dd, yyyy",
                                                Locale.getDefault()
                                            ).format(calendar.time)
                                        },
                                        calendar.get(Calendar.YEAR),
                                        calendar.get(Calendar.MONTH),
                                        calendar.get(Calendar.DAY_OF_MONTH)
                                    ).show()
                                }
                            )
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledBorderColor = Color.White.copy(alpha = 0.3f),
                            disabledTextColor = Color.White,
                            disabledPlaceholderColor = Color.White.copy(alpha = 0.4f),
                            disabledTrailingIconColor = Color(0xFF5AB9C1)
                        )
                    )

                    OutlinedTextField(
                        value = scheduledTimeText,
                        onValueChange = {},
                        readOnly = true,
                        enabled = false,
                        placeholder = {
                            Text(
                                text = "Time",
                                color = Color.White.copy(alpha = 0.4f)
                            )
                        },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = "Pick Time",
                                tint = Color(0xFF5AB9C1),
                                modifier = Modifier.clickable { showTimePicker = true }
                            )
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledBorderColor = Color.White.copy(alpha = 0.3f),
                            disabledTextColor = Color.White,
                            disabledPlaceholderColor = Color.White.copy(alpha = 0.4f),
                            disabledTrailingIconColor = Color(0xFF5AB9C1)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Color.White.copy(alpha = 0.05f),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Status",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
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
                            checkedTrackColor = Color(0xFF5AB9C1),
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color.White.copy(alpha = 0.3f)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                Button(
                    onClick = {
                        if (captionText.isBlank()) {
                            Toast.makeText(context, "Caption is required", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (scheduledDateText.isEmpty()) {
                            Toast.makeText(context, "Please select a date", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        isSaving = true

                        if (mediaUri != null) {
                            try {
                                MediaManager.get()
                                    .upload(mediaUri)
                                    .unsigned("planner")
                                    .callback(object : UploadCallback {
                                        override fun onStart(requestId: String?) {}
                                        override fun onProgress(
                                            requestId: String?,
                                            bytes: Long,
                                            totalBytes: Long
                                        ) {}

                                        override fun onSuccess(
                                            requestId: String?,
                                            resultData: Map<*, *>?
                                        ) {
                                            val url = resultData?.get("secure_url")?.toString() ?: ""
                                            saveToFirestore(
                                                db = db,
                                                auth = auth,
                                                postId = postId,
                                                caption = captionText,
                                                hashtags = hashtagText,
                                                platforms = selectedPlatforms,
                                                date = scheduledDateText,
                                                time = scheduledTimeText,
                                                isReady = isReadyForReview,
                                                imageUrl = url
                                            ) {
                                                // ✅ FIX: Post back to main thread
                                                mainHandler.post {
                                                    isSaving = false
                                                    navController.navigate("calendar") {
                                                        popUpTo("calendar") { inclusive = true }
                                                    }
                                                }
                                            }
                                        }

                                        override fun onError(
                                            requestId: String?,
                                            error: ErrorInfo?
                                        ) {
                                            // ✅ FIX: Post back to main thread
                                            mainHandler.post {
                                                isSaving = false
                                                Toast.makeText(
                                                    context,
                                                    "Upload failed: ${error?.description ?: "Unknown error"}",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }

                                        override fun onReschedule(
                                            requestId: String?,
                                            error: ErrorInfo?
                                        ) {
                                            // ✅ FIX: Post back to main thread
                                            mainHandler.post {
                                                isSaving = false
                                            }
                                        }
                                    })
                                    .dispatch()
                            } catch (e: Exception) {
                                isSaving = false
                                Toast.makeText(
                                    context,
                                    "Error: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            saveToFirestore(
                                db = db,
                                auth = auth,
                                postId = postId,
                                caption = captionText,
                                hashtags = hashtagText,
                                platforms = selectedPlatforms,
                                date = scheduledDateText,
                                time = scheduledTimeText,
                                isReady = isReadyForReview,
                                imageUrl = remoteMediaUrl ?: ""
                            ) {
                                // ✅ Firebase callbacks already run on main thread
                                isSaving = false
                                navController.navigate("calendar") {
                                    popUpTo("calendar") { inclusive = true }
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF5AB9C1),
                        disabledContainerColor = Color(0xFF5AB9C1).copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(15.dp),
                    enabled = !isSaving
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            color = Color(0xFF002B36),
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = if (postId == null) "Save Post" else "Update Post",
                            color = Color(0xFF002B36),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        if (showTimePicker) {
            AlertDialog(
                onDismissRequest = { showTimePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val cal = Calendar.getInstance().apply {
                                set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                                set(Calendar.MINUTE, timePickerState.minute)
                            }
                            scheduledTimeText = SimpleDateFormat(
                                "hh:mm a",
                                Locale.getDefault()
                            ).format(cal.time)
                            showTimePicker = false
                        }
                    ) {
                        Text(text = "OK", color = Color(0xFF5AB9C1))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showTimePicker = false }) {
                        Text(text = "Cancel", color = Color.White.copy(alpha = 0.6f))
                    }
                },
                containerColor = Color(0xFF002B36),
                text = {
                    TimePicker(
                        state = timePickerState,
                        colors = TimePickerDefaults.colors(
                            clockDialColor = Color(0xFF005F73),
                            selectorColor = Color(0xFF5AB9C1),
                            containerColor = Color(0xFF002B36),
                            periodSelectorBorderColor = Color(0xFF5AB9C1),
                            clockDialSelectedContentColor = Color(0xFF002B36),
                            clockDialUnselectedContentColor = Color.White,
                            timeSelectorSelectedContainerColor = Color(0xFF5AB9C1),
                            timeSelectorUnselectedContainerColor = Color.White.copy(alpha = 0.1f),
                            timeSelectorSelectedContentColor = Color(0xFF002B36),
                            timeSelectorUnselectedContentColor = Color.White
                        )
                    )
                }
            )
        }

        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            BottomNavBar(navController)
        }
    }
}

private fun saveToFirestore(
    db: FirebaseFirestore,
    auth: FirebaseAuth,
    postId: String?,
    caption: String,
    hashtags: String,
    platforms: Set<String>,
    date: String,
    time: String,
    isReady: Boolean,
    imageUrl: String,
    onComplete: () -> Unit
) {
    val userId = auth.currentUser?.uid ?: run {
        onComplete()
        return
    }

    val postData = mutableMapOf<String, Any>(
        "userId" to userId,
        "caption" to caption,
        "hashtags" to hashtags,
        "platforms" to platforms.toList(),
        "date" to date,
        "time" to time,
        "status" to if (isReady) "ready" else "draft",
        "imageUrl" to imageUrl,
        "updatedAt" to System.currentTimeMillis()
    )

    if (postId == null) {
        postData["createdAt"] = System.currentTimeMillis()
        db.collection("posts")
            .add(postData)
            .addOnSuccessListener { onComplete() }
            .addOnFailureListener { onComplete() }
    } else {
        db.collection("posts")
            .document(postId)
            .set(postData)
            .addOnSuccessListener { onComplete() }
            .addOnFailureListener { onComplete() }
    }
}