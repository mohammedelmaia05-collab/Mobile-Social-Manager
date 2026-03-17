package com.example.planner.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.planner.R
import com.example.planner.components.BottomNavBar

@Composable
fun PostDetailsScreen(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF002B36), Color(0xFF005F73))))) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBackIos, contentDescription = null, tint = Color.White)
                }
                Text("Post Details", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Main Post Image Preview
            Card(
                modifier = Modifier.fillMaxWidth().height(200.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize().background(Color.Gray)) {
                    Image(painter = painterResource(id = R.drawable.logo), contentDescription = null, modifier = Modifier.align(Alignment.Center).size(100.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Post Info
            Text("Instagram Post - March 23", color = Color(0xFF5AB9C1), fontWeight = FontWeight.Bold)
            Text("\"Summer launch! Ready to go live.\"", color = Color.White, fontSize = 16.sp)

            Spacer(modifier = Modifier.height(24.dp))

            // Manager Comments Box
            Text("Manager Comments", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
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
                        Text("Please add more hashtags and change the primary link in the bio before 5 PM.", color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = { /* Edit logic */ },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5AB9C1)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Edit Post", color = Color(0xFF002B36), fontWeight = FontWeight.Bold)
            }
        }
        Box(modifier = Modifier.align(Alignment.BottomCenter)) { BottomNavBar(navController) }
    }
}