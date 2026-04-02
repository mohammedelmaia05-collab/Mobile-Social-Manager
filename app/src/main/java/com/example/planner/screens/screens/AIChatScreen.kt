package com.example.planner.screens.screens

import android.util.Log
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

data class ChatMessage(val text: String, val isUser: Boolean)

@Composable
fun AIChatScreen(navController: NavController) {
    var messageText by remember { mutableStateOf("") }
    val chatMessages = remember { mutableStateListOf(ChatMessage("Hello! I'm your Social Media Assistant. How can I help you plan your content today?", false)) }
    val scope = rememberCoroutineScope()
    var isTyping by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    // 1. SYSTEM PROMPT: This defines your AI's personality and limits
    val generativeModel = remember {
        GenerativeModel(
            modelName = "gemini-2.5-flash",
            apiKey = "AIzaSyC52mxD1oVZcIE9s_sO619tfViue6_Xbno", // Keep your API key safe!
            systemInstruction = content {
                text("""
                    You are an expert Social Media Manager and Content Strategist built into the 'PLANNER' app.
                    Your personality is professional, creative, and highly encouraging.
                    
                    Your specific capabilities:
                    1. Writing engaging captions for Instagram, TikTok, LinkedIn, and Facebook.
                    2. Researching and suggesting trending hashtags.
                    3. Generating content ideas based on the user's niche.
                    4. Advising on the best times to post and visual styles.
                    
                    Rules:
                    - ALWAYS keep your answers focused on social media, marketing, or content creation.
                    - If a user asks a non-related question (e.g., math, history, coding), politely explain that your expertise is limited to helping them grow their social media presence.
                    - Use bullet points and emojis to make your advice easy to read.
                """.trimIndent())
            }
        )
    }

    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF002B36), Color(0xFF005F73))))) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBackIos, null, tint = Color.White)
                }
                Text("AI Assistant", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Chat History
            LazyColumn(
                modifier = Modifier.weight(1f),
                state = listState,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(chatMessages) { msg -> AIChatBubble(msg.text, msg.isUser) }
                if (isTyping) {
                    item {
                        Text("AI is typing...", color = Color(0xFF5AB9C1).copy(alpha = 0.7f), fontSize = 12.sp, modifier = Modifier.padding(start = 12.dp))
                    }
                }
            }

            // Input Area
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    placeholder = { Text("Ask for a caption or hashtags...", color = Color.White.copy(alpha = 0.4f)) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(25.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
                        focusedContainerColor = Color.White.copy(alpha = 0.15f),
                        cursorColor = Color(0xFF5AB9C1),
                        focusedBorderColor = Color(0xFF5AB9C1)
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (messageText.isNotBlank()) {
                            val userMsg = messageText
                            chatMessages.add(ChatMessage(userMsg, true))
                            messageText = ""
                            isTyping = true

                            scope.launch {
                                try {
                                    // The systemInstruction is already working in the background here
                                    val response = generativeModel.generateContent(userMsg)
                                    chatMessages.add(ChatMessage(response.text ?: "I'm having trouble thinking of a creative idea right now.", false))
                                } catch (e: Exception) {
                                    chatMessages.add(ChatMessage("Error: Check your internet connection.", false))
                                    Log.e("GeminiError", e.message ?: "Unknown Error")
                                } finally {
                                    isTyping = false
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .size(50.dp)
                        .background(if (isTyping || messageText.isBlank()) Color.Gray else Color(0xFF5AB9C1), RoundedCornerShape(50)),
                    enabled = !isTyping && messageText.isNotBlank()
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, null, tint = Color(0xFF002B36))
                }
            }
        }
    }
}

@Composable
fun AIChatBubble(text: String, isUser: Boolean) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start) {
        Card(
            modifier = Modifier.widthIn(max = 300.dp),
            shape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = if (isUser) 18.dp else 2.dp,
                bottomEnd = if (isUser) 2.dp else 18.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (isUser) Color(0xFF5AB9C1) else Color.White.copy(alpha = 0.12f)
            )
        ) {
            Text(
                text = text,
                color = if (isUser) Color(0xFF002B36) else Color.White,
                modifier = Modifier.padding(14.dp),
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}