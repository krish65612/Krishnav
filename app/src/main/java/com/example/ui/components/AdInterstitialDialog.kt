package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun AdInterstitialDialog(
    songCount: Int,
    isDarkTheme: Boolean,
    onDismiss: () -> Unit,
    onGetSubscription: () -> Unit
) {
    var secondsRemaining by remember { mutableStateOf(5) }
    val canSkip = secondsRemaining <= 0

    LaunchedEffect(Unit) {
        while (secondsRemaining > 0) {
            delay(1000)
            secondsRemaining -= 1
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ad_pulse"
    )

    Dialog(
        onDismissRequest = {
            if (canSkip) onDismiss()
        },
        properties = DialogProperties(dismissOnBackPress = canSkip, dismissOnClickOutside = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("ad_interstitial_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDarkTheme) ObsidianCardElevated else PureWhiteCardElevated
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Tag
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFFFB800).copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "SPONSORED BREAK (4 SONGS PLAYED)",
                            color = Color(0xFFFFB800),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (canSkip) {
                        IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = if (isDarkTheme) TextWhitePrimary else TextDarkPrimary
                            )
                        }
                    } else {
                        Text(
                            text = "${secondsRemaining}s",
                            color = if (isDarkTheme) TextWhiteSecondary else TextDarkSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Animated Ad Icon & Waveform
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(NeonCyan.copy(alpha = pulseAlpha), NeonMagenta.copy(alpha = pulseAlpha))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = "Audio Ad",
                        tint = Color.White,
                        modifier = Modifier.size(42.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Roland Audio Labs",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) TextWhitePrimary else TextDarkPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Powering independent musicians worldwide with analog synths & grooveboxes.",
                    fontSize = 13.sp,
                    color = if (isDarkTheme) TextWhiteSecondary else TextDarkSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Get Subscription to remove ads banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(NeonMagenta.copy(alpha = 0.15f))
                        .padding(14.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "VIP",
                                tint = NeonMagenta,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Tired of Ads? Get Backstage Pass",
                                color = NeonMagenta,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Enjoy 100% ad-free listening, 24-bit lossless audio, and unreleased stems for just $4.99/mo.",
                            fontSize = 11.sp,
                            color = if (isDarkTheme) TextWhiteSecondary else TextDarkSecondary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                onDismiss()
                                onGetSubscription()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonMagenta),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("ad_upgrade_subscription_btn")
                        ) {
                            Text(
                                text = "Remove Ads with Subscription",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Skip button
                OutlinedButton(
                    onClick = onDismiss,
                    enabled = canSkip,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (canSkip) "Continue Listening (Skip Ad)" else "Ad playing... (${secondsRemaining}s)",
                        fontSize = 12.sp,
                        color = if (canSkip) (if (isDarkTheme) TextWhitePrimary else TextDarkPrimary) else (if (isDarkTheme) TextWhiteSecondary else TextDarkSecondary)
                    )
                }
            }
        }
    }
}
