package com.example.planner.screens

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.planner.R
import com.example.planner.components.SocialButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

@Composable
fun LoginScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // --- GOOGLE SIGN IN LOGIC ---
    // Get your Web Client ID from Firebase Console -> Authentication -> Google Provider
    val webClientId = "810844623946-n6v3o88rk0qs5phca1fark3g95il1ddh.apps.googleusercontent.com"

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                auth.signInWithCredential(credential)
                    .addOnSuccessListener {
                        navController.navigate("calendar") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                    .addOnFailureListener {
                        isLoading = false
                        Toast.makeText(context, "Firebase Auth Failed: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            } catch (e: ApiException) {
                isLoading = false
                Toast.makeText(context, "Google Sign-In Failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            isLoading = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF002B36), Color(0xFF004B59))))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp).fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier.size(80.dp)
            )
            Text(
                "SOCIAL MEDIA\nPLANNER & CALENDAR",
                color = Color.White,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(60.dp))
            Text("Login", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(40.dp))

            // Email & Password Fields (Keep your PlannerInputField component)
            PlannerInputField(value = email, onValueChange = { email = it }, placeholder = "Email Address")
            Spacer(modifier = Modifier.height(16.dp))
            PlannerInputField(value = password, onValueChange = { password = it }, placeholder = "Password", isPassword = true)

            Spacer(modifier = Modifier.height(30.dp))

            // Login Button
            Button(
                onClick = {
                    if (email.isNotEmpty() && password.isNotEmpty()) {
                        isLoading = true
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnSuccessListener {
                                navController.navigate("calendar") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                            .addOnFailureListener {
                                isLoading = false
                                Toast.makeText(context, "Login Failed: ${it.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(55.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5AB9C1)), // Teal to match your theme
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading
            ) {
                if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                else Text("Login", color = Color(0xFF002B36), fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("or login with", color = Color.White.copy(alpha = 0.7f))

            // Social Icons Row
            Row(
                modifier = Modifier.padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // FIXED GOOGLE BUTTON
                Box(modifier = Modifier.clickable {
                    isLoading = true
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(webClientId)
                        .requestEmail()
                        .build()
                    val client = GoogleSignIn.getClient(context, gso)
                    launcher.launch(client.signInIntent)
                }) {
                    SocialButton(icon = R.drawable.google)
                }

                SocialButton(icon = R.drawable.github)
                SocialButton(icon = R.drawable.ic_facebook)
            }

            Spacer(modifier = Modifier.weight(1f))
            TextButton(onClick = { navController.navigate("register") }) {
                Text("Don't have an account? Register", color = Color(0xFF5AB9C1))
            }
        }
    }
}