package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CommunityPost
import com.example.model.Conversation
import com.example.ui.theme.*

@Composable
fun SocialAndMessagesScreen(
    conversations: List<Conversation>,
    communityPosts: List<CommunityPost>,
    profanityFilterEnabled: Boolean,
    isDarkTheme: Boolean,
    onOpenConversation: (String) -> Unit,
    onPostLike: (String) -> Unit,
    onToggleProfanityFilter: () -> Unit,
    onTriggerReleaseAlert: () -> Unit,
    onTriggerTourAlert: () -> Unit
) {
    var selectedSection by remember { mutableStateOf(0) } // 0: Direct Messages, 1: Community Feed, 2: Moderation & Alerts

    val bgColor = if (isDarkTheme) ObsidianBlack else PureWhite
    val cardBg = if (isDarkTheme) ObsidianCard else PureWhiteCardElevated
    val textColor = if (isDarkTheme) TextWhitePrimary else TextDarkPrimary
    val textSub = if (isDarkTheme) TextWhiteSecondary else TextDarkSecondary

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .padding(horizontal = 16.dp)
            .testTag("social_messages_screen")
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Artist & Fan Hub",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Text(
                text = "Direct messaging, broadcasts & community engagement",
                fontSize = 12.sp,
                color = textSub
            )
            Spacer(modifier = Modifier.height(14.dp))

            // Sub-tabs
            TabRow(
                selectedTabIndex = selectedSection,
                containerColor = Color.Transparent,
                contentColor = textColor,
                divider = {}
            ) {
                Tab(
                    selected = selectedSection == 0,
                    onClick = { selectedSection = 0 },
                    text = { Text("Messages & DMs", fontSize = 12.sp, fontWeight = if (selectedSection == 0) FontWeight.Bold else FontWeight.Normal) }
                )
                Tab(
                    selected = selectedSection == 1,
                    onClick = { selectedSection = 1 },
                    text = { Text("Community", fontSize = 12.sp, fontWeight = if (selectedSection == 1) FontWeight.Bold else FontWeight.Normal) }
                )
                Tab(
                    selected = selectedSection == 2,
                    onClick = { selectedSection = 2 },
                    text = { Text("Tools & Alerts", fontSize = 12.sp, fontWeight = if (selectedSection == 2) FontWeight.Bold else FontWeight.Normal) }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        when (selectedSection) {
            0 -> {
                // Direct Messages List & Broadcast Channels
                items(conversations) { conv ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onOpenConversation(conv.id) }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                        ) {
                            Image(
                                painter = painterResource(id = conv.avatarRes),
                                contentDescription = conv.artistName,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = conv.artistName,
                                    color = textColor,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                if (conv.isVerified) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Verified",
                                        tint = NeonCyan,
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                                if (conv.isBroadcastChannel) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(NeonMagenta.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(text = "BROADCAST", fontSize = 8.sp, color = NeonMagenta, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = conv.lastMessage,
                                color = textSub,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = conv.lastMessageTime, fontSize = 10.sp, color = textSub)
                            if (conv.unreadCount > 0) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(NeonCyan),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "${conv.unreadCount}", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                    HorizontalDivider(color = textSub.copy(alpha = 0.1f), modifier = Modifier.padding(start = 62.dp))
                }
            }

            1 -> {
                // Community Feed
                items(communityPosts) { post ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(id = post.avatarRes),
                                    contentDescription = post.artistName,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = post.artistName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = textColor
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(13.dp))
                                    }
                                    Text(text = "${post.artistHandle} • ${post.timestamp}", fontSize = 11.sp, color = textSub)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = post.content,
                                color = textColor,
                                fontSize = 13.sp,
                                lineHeight = 19.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { onPostLike(post.id) }
                                ) {
                                    Icon(
                                        imageVector = if (post.isLiked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                                        contentDescription = "Like",
                                        tint = if (post.isLiked) LiveGreen else textSub,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "${post.likes}", color = textSub, fontSize = 12.sp)
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Outlined.ChatBubbleOutline, contentDescription = "Comments", tint = textSub, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "${post.commentsCount}", color = textSub, fontSize = 12.sp)
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Outlined.Repeat, contentDescription = "Reposts", tint = textSub, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "${post.repostsCount}", color = textSub, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // Artist Moderation Tools & Push Alert Triggers
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Artist Comment Moderation",
                                color = textColor,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Automated protection filters for artist profiles & track comments.",
                                color = textSub,
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "Profanity & Spam Auto-Filter", fontSize = 13.sp, color = textColor, fontWeight = FontWeight.SemiBold)
                                    Text(text = "Hide abusive words, spam links, and bot comments", fontSize = 11.sp, color = textSub)
                                }
                                Switch(
                                    checked = profanityFilterEnabled,
                                    onCheckedChange = { onToggleProfanityFilter() },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = LiveGreen)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Test Push Notification Alerts",
                                color = textColor,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Trigger realistic new song drops and tour announcements to audiences.",
                                color = textSub,
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = onTriggerReleaseAlert,
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("New Release Alert", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Button(
                                    onClick = onTriggerTourAlert,
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonMagenta),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Tour Drop Alert", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}
