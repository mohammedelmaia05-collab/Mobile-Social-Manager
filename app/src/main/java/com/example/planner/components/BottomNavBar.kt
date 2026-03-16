package com.example.planner.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun BottomNavBar(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color(0xFF0D4C5F).copy(alpha = 0.9f), RoundedCornerShape(25.dp))
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavItem(Icons.Default.DateRange, "Calendar", true) { }
        NavItem(Icons.Default.BarChart, "Analytics", false) { }

        // Center Create Button
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(Color(0xFF5AB9C1), RoundedCornerShape(12.dp))
                .clickable { /* Action */ },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(30.dp))
        }

        NavItem(Icons.Default.Groups, "Community", false) { }
        NavItem(Icons.Default.Person, "Profile", false) { }
    }
}

@Composable
fun NavItem(icon: ImageVector, label: String, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) Color(0xFF5AB9C1) else Color.White.copy(alpha = 0.6f),
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            color = if (isSelected) Color(0xFF5AB9C1) else Color.White.copy(alpha = 0.6f),
            fontSize = 10.sp
        )
    }
}
