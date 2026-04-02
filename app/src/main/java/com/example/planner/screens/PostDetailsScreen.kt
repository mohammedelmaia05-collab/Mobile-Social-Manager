package com.example.planner.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun PostDetailsScreen(navController: NavController, postId: String) {
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current

    var caption by remember { mutableStateOf("Loading...") }
    var hashtags by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var platform by remember { mutableStateOf("") }
    var postImageUrl by remember { mutableStateOf<String?>(null) } // To hold the image URL
    var managerComment by remember { mutableStateOf("Please add more hashtags and change the primary link in the bio before 5 PM.") }

    LaunchedEffect(postId) {
        db.collection("posts").document(postId).get().addOnSuccessListener { doc ->
            if (doc.exists()) {
                caption = doc.getString("caption") ?: ""
                hashtags = doc.getString("hashtags") ?: ""
                date = doc.getString("date") ?: ""
                time = doc.getString("time") ?: ""
                postImageUrl = doc.getString("imageUrl") // Get the saved image URL

                val pList = doc.get("platforms") as? List<*>
                platform = pList?.filterIsInstance<String>()?.joinToString(", ") ?: "Social"
                managerComment = doc.getString("managerComment") ?: managerComment
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF002B36), Color(0xFF005F73))))) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {

            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBackIos, contentDescription = null, tint = Color.White)
                }
                Text("Post Details", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Main Post Image Preview
            Card(
                modifier = Modifier.fillMaxWidth().height(300.dp), // Made slightly taller to fit social images better
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.2f))) {
                    if (postImageUrl != null) {
                        // Display the actual uploaded image
                        AsyncImage(
                            model = postImageUrl,
                            contentDescription = "Post Image",
                            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(20.dp)),
                            contentScale = ContentScale.Crop // Fills the box cleanly
                        )
                    } else {
                        // Fallback to the logo if there's no image attached to the post
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "Placeholder",
                            modifier = Modifier.align(Alignment.Center).size(100.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Text content
            Text("$platform • $date", color = Color(0xFF5AB9C1), fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text("\"$caption\"", color = Color.White, fontSize = 18.sp, lineHeight = 24.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(hashtags, color = Color(0xFF5AB9C1), fontSize = 14.sp)

            Spacer(modifier = Modifier.height(30.dp))

            // Manager Comments Box
            Text("Manager Comments", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(15.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                    Icon(Icons.Default.ChatBubbleOutline, contentDescription = null, tint = Color(0xFF5AB9C1))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Mohammed Elamaia", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(managerComment, color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Action Buttons
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = {
                        db.collection("posts").document(postId).delete().addOnSuccessListener {
                            Toast.makeText(context, "Post is deleted", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier.weight(1f).height(55.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.8f)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Delete", color = Color.White, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { navController.navigate("create_post?postId=$postId") },
                    modifier = Modifier.weight(1f).height(55.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5AB9C1)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = Color(0xFF002B36))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Edit Post", color = Color(0xFF002B36), fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }

        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            BottomNavBar(navController)
        }
    }
}