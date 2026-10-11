package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Conversation
import com.example.ui.theme.*

@Composable
fun ConversationDetailScreen(
    conversation: Conversation,
    isDarkTheme: Boolean,
    onBack: () -> Unit,
    onSendMessage: (String) -> Unit
) {
    var messageText by remember { mutableStateOf("") }

    val bgColor = if (isDarkTheme) ObsidianBlack else PureWhite
    val textColor = if (isDarkTheme) TextWhitePrimary else TextDarkPrimary
    val textSub = if (isDarkTheme) TextWhiteSecondary else TextDarkSecondary

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .testTag("conversation_detail_screen")
    ) {
        // App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = textColor)
            }

            Image(
                painter = painterResource(id = conversation.avatarRes),
                contentDescription = conversation.artistName,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = conversation.artistName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = textColor
                    )
                    if (conversation.isVerified) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(13.dp))
                    }
                }
                Text(
                    text = if (conversation.isBroadcastChannel) "Official Broadcast Channel" else "${conversation.artistHandle} • Active on Vibelio",
                    fontSize = 11.sp,
                    color = textSub
                )
            }
        }

        HorizontalDivider(color = textSub.copy(alpha = 0.15f))

        // Message List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(conversation.messages) { msg ->
                val isMe = msg.isFromUser
                val bubbleBg = if (isMe) NeonCyan else (if (isDarkTheme) ObsidianCard else PureWhiteCardElevated)
                val bubbleText = if (isMe) Color.Black else textColor

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
                ) {
                    Box(
                        modifier = Modifier
                            .widthIn(max = 280.dp)
                            .clip(
                                RoundedCornerShape(
                                    topStart = 14.dp,
                                    topEnd = 14.dp,
                                    bottomStart = if (isMe) 14.dp else 2.dp,
                                    bottomEnd = if (isMe) 2.dp else 14.dp
                                )
                            )
                            .background(bubbleBg)
                            .padding(12.dp)
                    ) {
                        Column {
                            if (!isMe && !conversation.isBroadcastChannel) {
                                Text(
                                    text = msg.senderName,
                                    color = NeonMagenta,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                            }
                            Text(
                                text = msg.text,
                                color = bubbleText,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = msg.timestamp,
                                color = bubbleText.copy(alpha = 0.6f),
                                fontSize = 9.sp,
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }
                }
            }
        }

        // Message Input
        if (!conversation.isBroadcastChannel) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    placeholder = { Text("Send direct message to artist...", fontSize = 12.sp) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (messageText.isNotBlank()) {
                            onSendMessage(messageText)
                            messageText = ""
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(NeonCyan)
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.Black, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}
