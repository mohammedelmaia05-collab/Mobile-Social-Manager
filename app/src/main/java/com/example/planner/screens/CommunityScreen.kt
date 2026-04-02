package com.example.planner.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun CommunityScreen(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current

    var inviteEmail by remember { mutableStateOf("") }
    var teamMembers by remember { mutableStateOf(listOf<Map<String, Any>>()) }
    var isLoading by remember { mutableStateOf(false) }

    // Fetch team members based on the current user's teamId
    LaunchedEffect(Unit) {
        val currentUser = auth.currentUser?.uid
        if (currentUser != null) {
            db.collection("users").document(currentUser).get()
                .addOnSuccessListener { doc ->
                    val teamId = doc.getString("teamId") ?: ""
                    if (teamId.isNotEmpty()) {
                        db.collection("users")
                            .whereEqualTo("teamId", teamId)
                            .addSnapshotListener { snapshot, _ ->
                                if (snapshot != null) {
                                    teamMembers = snapshot.documents.map { it.data ?: emptyMap() }
                                }
                            }
                    }
                }
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Brush.verticalGradient(listOf(Color(0xFF002B36), Color(0xFF005F73))))
    ) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text("My Team", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text("Invite collaborators to your planner", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)

            Spacer(modifier = Modifier.height(30.dp))

            // Invitation Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Invite Member", color = Color(0xFF5AB9C1), fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = inviteEmail,
                        onValueChange = { inviteEmail = it },
                        placeholder = { Text("Enter email address", color = Color.White.copy(alpha = 0.4f)) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF5AB9C1),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (inviteEmail.isNotEmpty()) {
                                isLoading = true
                                val inviteData = hashMapOf(
                                    "email" to inviteEmail,
                                    "invitedBy" to (auth.currentUser?.email ?: ""),
                                    "status" to "pending"
                                )
                                db.collection("invites").add(inviteData)
                                    .addOnSuccessListener {
                                        isLoading = false
                                        Toast.makeText(context, "Invite sent!", Toast.LENGTH_SHORT).show()
                                        inviteEmail = ""
                                    }
                                    .addOnFailureListener {
                                        isLoading = false
                                        Toast.makeText(context, "Failed: ${it.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5AB9C1)),
                        shape = RoundedCornerShape(10.dp),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            // FIXED: Use modifier for size in Material3
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color(0xFF002B36),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Send Invitation", color = Color(0xFF002B36), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text("Current Members", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(teamMembers) { member ->
                    MemberRow(
                        name = member["name"]?.toString() ?: "Member",
                        role = member["role"]?.toString() ?: "Editor",
                        email = member["email"]?.toString() ?: ""
                    )
                }
            }

            Spacer(modifier = Modifier.height(80.dp)) // Space for Bottom Bar
        }

        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            BottomNavBar(navController)
        }
    }
}

@Composable
fun MemberRow(name: String, role: String, email: String) {
    Surface(
        color = Color.White.copy(alpha = 0.05f),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFF5AB9C1).copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF5AB9C1))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(email, color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
            }
            Spacer(modifier = Modifier.weight(1f))
            Surface(
                color = Color(0xFF5AB9C1).copy(alpha = 0.1f),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = role,
                    color = Color(0xFF5AB9C1),
                    fontSize = 10.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
