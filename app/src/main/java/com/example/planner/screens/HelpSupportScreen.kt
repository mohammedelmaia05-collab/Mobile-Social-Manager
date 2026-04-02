package com.example.planner.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.Send
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
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.launch
import android.util.Log


// Data model for messages
data class ChatMessage(
    val text: String,
    val isUser: Boolean
)

@Composable
fun HelpSupportScreen(navController: NavController) {
    // 1. Initialize Gemini (Get your key at aistudio.google.com)
    val generativeModel = remember {
        GenerativeModel(
            modelName = "gemini-2.5-flash",
            apiKey = "AIzaSyC52mxD1oVZcIE9s_sO619tfViue6_Xbno"
        )
    }

    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // Chat History State
    var userInput by remember { mutableStateOf("") }
    val chatMessages = remember {
        mutableStateListOf(ChatMessage("Hello! I'm your Social Media Planner AI. How can I help you today?", false))
    }
    var isTyping by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF002B36), Color(0xFF005F73))))) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBackIos, contentDescription = null, tint = Color.White)
                }
                Text("AI Planner Assistant", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Chat Area
            LazyColumn(
                modifier = Modifier.weight(1f),
                state = listState,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(chatMessages) { message ->
                    ChatBubble(message.text, message.isUser)
                }
                if (isTyping) {
                    item { Text("AI is thinking...", color = Color(0xFF5AB9C1), fontSize = 12.sp, modifier = Modifier.padding(start = 8.dp)) }
                }
            }

            // 3. Input Bar Logic
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = userInput,
                    onValueChange = { userInput = it },
                    placeholder = { Text("Ask about hashtags, captions, or strategy...", color = Color.White.copy(alpha = 0.4f)) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(25.dp),
                    enabled = !isTyping,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
                        focusedContainerColor = Color.White.copy(alpha = 0.15f),
                        focusedBorderColor = Color(0xFF5AB9C1)
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (userInput.isNotBlank()) {
                            val userText = userInput
                            chatMessages.add(ChatMessage(userText, true))
                            userInput = ""
                            isTyping = true

                            // Scroll to bottom
                            scope.launch { listState.animateScrollToItem(chatMessages.size - 1) }

                            // 4. Call AI
                            scope.launch {
                                try {
                                    val response = generativeModel.generateContent(
                                        content { text(userText) }
                                    )
                                    chatMessages.add(ChatMessage(response.text ?: "Sorry, I couldn't process that.", false))
                                } catch (e: Exception) {
                                    Log.e("HelpSupport", "AI Error: ${e.message}", e) // This prints the REAL error to Logcat
                                    chatMessages.add(ChatMessage("Error: ${e.localizedMessage}", false))
                                } finally {
                                    isTyping = false
                                    // ...
                                }
                            }
                        }
                    },
                    modifier = Modifier.background(Color(0xFF5AB9C1), RoundedCornerShape(50)),
                    enabled = !isTyping && userInput.isNotBlank()
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = Color(0xFF002B36))
                }
            }
        }
    }
}

@Composable
fun ChatBubble(text: String, isUser: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            shape = RoundedCornerShape(
                topStart = 15.dp,
                topEnd = 15.dp,
                bottomStart = if (isUser) 15.dp else 0.dp,
                bottomEnd = if (isUser) 0.dp else 15.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (isUser) Color(0xFF5AB9C1) else Color.White.copy(alpha = 0.1f)
            ),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Text(
                text = text,
                color = if (isUser) Color.Black else Color.White,
                modifier = Modifier.padding(12.dp),
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}