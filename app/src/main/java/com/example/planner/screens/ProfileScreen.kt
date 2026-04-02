package com.example.planner.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.planner.R
import com.example.planner.components.BottomNavBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

@Composable
fun ProfileScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()
    val context = LocalContext.current

    var userName by remember { mutableStateOf("Mohammed Elamaia") }
    var userRole by remember { mutableStateOf("Digital Developer") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var savedImageUrl by remember { mutableStateOf<String?>(null) }
    var isUploading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        userName = document.getString("name") ?: "Mohammed Elamaia"
                        userRole = document.getString("role") ?: "Digital Developer"
                        savedImageUrl = document.getString("profileImage")
                    }
                }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri = it
            isUploading = true
            val userId = auth.currentUser?.uid
            if (userId != null) {
                val storageRef = storage.reference.child("profile_images/$userId.jpg")
                storageRef.putFile(it)
                    .addOnSuccessListener {
                        storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                            db.collection("users").document(userId)
                                .update("profileImage", downloadUrl.toString())
                                .addOnSuccessListener {
                                    isUploading = false
                                    savedImageUrl = downloadUrl.toString()
                                    Toast.makeText(context, "Profile picture saved!", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                    .addOnFailureListener { e ->
                        isUploading = false
                        Toast.makeText(context, "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
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
                Text("Profile", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.12f))
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color.Gray.copy(alpha = 0.2f), CircleShape)
                            .clickable { galleryLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (imageUri != null) {
                            AsyncImage(model = imageUri, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize().clip(CircleShape))
                        } else if (savedImageUrl != null) {
                            AsyncImage(model = savedImageUrl, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize().clip(CircleShape))
                        } else {
                            Image(painter = painterResource(id = R.drawable.logo), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize().clip(CircleShape))
                        }

                        if (isUploading) {
                            CircularProgressIndicator(modifier = Modifier.size(40.dp), color = Color(0xFF5AB9C1))
                        }

                        Box(modifier = Modifier.align(Alignment.BottomEnd).background(Color(0xFF5AB9C1), CircleShape).padding(6.dp)) {
                            Icon(Icons.Default.Edit, contentDescription = null, tint = Color(0xFF002B36), modifier = Modifier.size(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(userName, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(userRole, color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Account Settings", color = Color(0xFF5AB9C1), fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))

            SettingsRow(Icons.Default.Person, "Edit Profile") { navController.navigate("edit_profile") }
            SettingsRow(Icons.Default.Lock, "Change Password") { navController.navigate("change_password") }
            SettingsRow(Icons.Default.Notifications, "Notifications") { navController.navigate("notifications") }

            Spacer(modifier = Modifier.height(20.dp))

            Text("App Settings", color = Color(0xFF5AB9C1), fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))

            SettingsRow(Icons.Default.DarkMode, "Dark Mode") { /* Toggle Logic */ }
            SettingsRow(Icons.Default.Language, "Language") { navController.navigate("language") }
            SettingsRow(Icons.Default.Info, "Help & Support") { navController.navigate("help") }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    auth.signOut()
                    navController.navigate("login") {
                        popUpTo("welcome") { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp).padding(bottom = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB00020)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Logout", color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(80.dp))
        }
        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            BottomNavBar(navController)
        }
    }
}

@Composable
fun SettingsRow(icon: ImageVector, title: String, onClick: () -> Unit) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(title, color = Color.White, modifier = Modifier.weight(1f), fontSize = 15.sp)
            Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = Color.White.copy(alpha = 0.4f), modifier = Modifier.size(14.dp))
        }
        HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
    }
}
