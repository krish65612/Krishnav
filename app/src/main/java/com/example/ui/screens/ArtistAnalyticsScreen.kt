package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Demographics
import com.example.model.SubscriptionTier
import com.example.ui.theme.*

@Composable
fun ArtistAnalyticsScreen(
    demographics: Demographics,
    subscriptionTiers: List<SubscriptionTier>,
    currentSubscribedTierId: String,
    timeRange: String,
    isDarkTheme: Boolean,
    onSelectTimeRange: (String) -> Unit,
    onPromptSubscribe: (SubscriptionTier) -> Unit,
    onRequestPayout: () -> Unit
) {
    val bgColor = if (isDarkTheme) ObsidianBlack else PureWhite
    val cardBg = if (isDarkTheme) ObsidianCard else PureWhiteCardElevated
    val textColor = if (isDarkTheme) TextWhitePrimary else TextDarkPrimary
    val textSub = if (isDarkTheme) TextWhiteSecondary else TextDarkSecondary

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .padding(horizontal = 16.dp)
            .testTag("analytics_screen")
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Artist & Streaming Analytics",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                    Text(
                        text = "Real-time metrics for independent creators",
                        fontSize = 12.sp,
                        color = textSub
                    )
                }

                // Time Range Filter
                val ranges = listOf("7D", "30D", "All")
                Row {
                    ranges.forEach { range ->
                        val isSelected = (range == "7D" && timeRange.contains("7")) ||
                                         (range == "30D" && timeRange.contains("30"))
                        Box(
                            modifier = Modifier
                                .padding(start = 4.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) NeonCyan else Color.Transparent)
                                .clickable { onSelectTimeRange(if (range == "7D") "Last 7 Days" else "Last 30 Days") }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = range,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.Black else textSub
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
        }

        // Live Real-Time Stream Ticker
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = cardBg),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TOTAL LIVE STREAMS",
                            color = textSub,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(LiveGreen.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "● LIVE TICKER",
                                color = LiveGreen,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "2,465,300",
                        color = textColor,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "+18.4% growth vs previous period • 84% completion rate",
                        color = NeonCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Hourly Stream Trend Bars
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        demographics.hourlyStreams.forEach { (hour, count) ->
                            val maxCount = 8420f
                            val barFraction = (count / maxCount).coerceIn(0.15f, 1f)
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(20.dp)
                                        .fillMaxHeight(barFraction)
                                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                        .background(Brush.verticalGradient(listOf(NeonCyan, NeonMagenta)))
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = hour.take(2), fontSize = 9.sp, color = textSub)
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Listener Geographic Demographics
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = cardBg),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Public, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Top Listener Geographies",
                            color = textColor,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))

                    demographics.topCountries.forEach { (country, percent) ->
                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = country, fontSize = 12.sp, color = textColor)
                                Text(text = "$percent%", fontSize = 12.sp, color = textSub, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { percent / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(5.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = NeonCyan,
                                trackColor = textSub.copy(alpha = 0.2f),
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Age & Demographics Split
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = cardBg),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Listener Age & Demographic Split",
                        color = textColor,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        demographics.ageDistribution.forEach { (ageBracket, percent) ->
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(textColor.copy(alpha = 0.05f))
                                    .padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "$percent%", color = NeonMagenta, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(text = ageBracket, color = textSub, fontSize = 10.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        demographics.genderSplit.forEach { (gender, percent) ->
                            Text(text = "$gender: $percent%", color = textSub, fontSize = 11.sp)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Royalties & Instant Payout
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = cardBg),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ROYALTIES & SUBSCRIPTIONS EARNED",
                            color = textSub,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(Icons.Outlined.AccountBalanceWallet, contentDescription = null, tint = AccentGold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "$${String.format("%.2f", demographics.totalRevenueEarned)}",
                        color = textColor,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Pending payout balance: $${String.format("%.2f", demographics.pendingPayoutBalance)}",
                        color = AccentGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onRequestPayout,
                        colors = ButtonDefaults.buttonColors(containerColor = LiveGreen),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("request_payout_btn"),
                        enabled = demographics.pendingPayoutBalance > 0
                    ) {
                        Text(
                            text = if (demographics.pendingPayoutBalance > 0) "Request Instant Payout Wire" else "Payout Dispatched",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Subscription Model & ₹65 Plan
        item {
            Text(
                text = "Support Independent Artists",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Spacer(modifier = Modifier.height(10.dp))

            subscriptionTiers.filterNot { it.id == "tier_free" }.forEach { tier ->
                val isSubscribed = currentSubscribedTierId == tier.id
                Card(
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = tier.name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor
                                )
                                Text(text = tier.description, fontSize = 11.sp, color = textSub)
                            }
                            Text(
                                text = tier.priceDisplay,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonMagenta
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        tier.perks.forEach { perk ->
                            Row(
                                modifier = Modifier.padding(vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = LiveGreen, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = perk, fontSize = 11.sp, color = textSub)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (isSubscribed) {
                            Button(
                                onClick = { },
                                enabled = false,
                                colors = ButtonDefaults.buttonColors(containerColor = LiveGreen),
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Active Membership ✓", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        } else {
                            Button(
                                onClick = { onPromptSubscribe(tier) },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonMagenta),
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Get ${tier.name} (${tier.priceDisplay})", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}
