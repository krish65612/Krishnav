package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Tour
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppNotification
import com.example.model.NotificationType
import com.example.ui.theme.*

@Composable
fun NotificationsScreen(
    notifications: List<AppNotification>,
    isDarkTheme: Boolean,
    onBack: () -> Unit,
    onMarkAllRead: () -> Unit,
    onNotificationClick: (AppNotification) -> Unit
) {
    var selectedFilter by remember { mutableStateOf("All") }

    val bgColor = if (isDarkTheme) ObsidianBlack else PureWhite
    val cardBg = if (isDarkTheme) ObsidianCard else PureWhiteCardElevated
    val textColor = if (isDarkTheme) TextWhitePrimary else TextDarkPrimary
    val textSub = if (isDarkTheme) TextWhiteSecondary else TextDarkSecondary

    val filteredList = remember(selectedFilter, notifications) {
        when (selectedFilter) {
            "Releases" -> notifications.filter { it.type == NotificationType.NEW_RELEASE }
            "Tours" -> notifications.filter { it.type == NotificationType.TOUR_ALERT }
            else -> notifications
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .padding(horizontal = 16.dp)
            .testTag("notifications_screen")
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = textColor)
                }
                Text(
                    text = "Release & Tour Alerts",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }

            TextButton(onClick = onMarkAllRead) {
                Text("Mark Read", fontSize = 12.sp, color = NeonCyan)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Filter chips
        Row {
            listOf("All", "Releases", "Tours").forEach { filter ->
                val isSelected = selectedFilter == filter
                Box(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) (if (isDarkTheme) Color(0xFF2E2E38) else Color(0xFFE2E4E9)) else Color.Transparent)
                        .clickable { selectedFilter = filter }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = filter,
                        color = textColor,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (filteredList.isEmpty()) {
                item {
                    Text(
                        text = "You're all caught up with your indie artists!",
                        color = textSub,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(vertical = 24.dp)
                    )
                }
            } else {
                items(filteredList) { notif ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNotificationClick(notif) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val (icon, tint) = when (notif.type) {
                                NotificationType.NEW_RELEASE -> Icons.Outlined.LibraryMusic to NeonCyan
                                NotificationType.TOUR_ALERT -> Icons.Default.Tour to AccentGold
                                NotificationType.EXCLUSIVE_DROP -> Icons.Outlined.Star to NeonMagenta
                                else -> Icons.Default.Notifications to LiveGreen
                            }

                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(tint.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = notif.title,
                                    color = textColor,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = notif.body,
                                    color = textSub,
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = notif.timestamp,
                                    color = textSub.copy(alpha = 0.6f),
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}
