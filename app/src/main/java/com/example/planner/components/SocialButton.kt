package com.example.planner.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun SocialButton(icon: Int) {

    Box(
        modifier = Modifier
            .size(60.dp)
            .background(
                color = Color.White,
                shape = RoundedCornerShape(10.dp)
            ),
        contentAlignment = Alignment.Center
    ) {

        Image(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(30.dp)
        )

    }
}