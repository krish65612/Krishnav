package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Artist
import com.example.model.Comment
import com.example.model.SubscriptionTier
import com.example.model.Track
import com.example.ui.components.CommentItem
import com.example.ui.theme.*

@Composable
fun ArtistProfileScreen(
    artist: Artist,
    tracks: List<Track>,
    comments: List<Comment>,
    isFollowed: Boolean,
    userTierId: String,
    isDarkTheme: Boolean,
    onBack: () -> Unit,
    onToggleFollow: () -> Unit,
    onTrackClick: (Track) -> Unit,
    onPromptSubscribe: (SubscriptionTier) -> Unit,
    onAddComment: (String, String, String) -> Unit,
    onToggleCommentLike: (String) -> Unit,
    onPinComment: (String) -> Unit,
    onHideComment: (String) -> Unit
) {
    val context = LocalContext.current
    val artistTracks = tracks.filter { it.artistId == artist.id }
    val artistComments = comments.filter { it.targetType == "ARTIST" && it.targetId == artist.id }

    var newCommentText by remember { mutableStateOf("") }

    val bgColor = if (isDarkTheme) ObsidianBlack else PureWhite
    val cardBg = if (isDarkTheme) ObsidianCard else PureWhiteCardElevated
    val textColor = if (isDarkTheme) TextWhitePrimary else TextDarkPrimary
    val textSub = if (isDarkTheme) TextWhiteSecondary else TextDarkSecondary

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .testTag("artist_profile_screen")
    ) {
        // Hero Concert Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            ) {
                Image(
                    painter = painterResource(id = artist.bannerRes),
                    contentDescription = artist.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .padding(16.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f))
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
            }
        }

        // Artist Info & Bio
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = artist.name,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.Default.CheckCircle, contentDescription = "Verified Indie", tint = NeonCyan, modifier = Modifier.size(18.dp))
                        }
                        Text(text = "${artist.handle} • ${artist.monthlyListeners / 1000}k monthly listeners", fontSize = 12.sp, color = textSub)
                    }

                    Button(
                        onClick = onToggleFollow,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isFollowed) (if (isDarkTheme) Color(0xFF333333) else Color(0xFFE5E7EB)) else NeonCyan
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = if (isFollowed) "Following" else "Follow",
                            color = if (isFollowed) textColor else Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Instagram Connected Link Chip
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFE1306C).copy(alpha = 0.15f))
                        .clickable {
                            val igIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/${artist.handle.removePrefix("@")}"))
                            context.startActivity(Intent.createChooser(igIntent, "Open Instagram"))
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.Share, contentDescription = "Instagram", tint = Color(0xFFE1306C), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Instagram: ${artist.handle}",
                        color = Color(0xFFE1306C),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = artist.bio,
                    color = textColor.copy(alpha = 0.9f),
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Popular Tracks",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        // Popular Tracks
        items(artistTracks) { track ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onTrackClick(track) }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = track.coverRes),
                    contentDescription = track.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(6.dp))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = track.title, color = textColor, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Text(text = "${track.streamCount / 1000}k streams • ${track.durationSeconds / 60}:${String.format("%02d", track.durationSeconds % 60)}", color = textSub, fontSize = 11.sp)
                }
                Icon(Icons.Default.PlayCircle, contentDescription = "Play", tint = NeonCyan, modifier = Modifier.size(28.dp))
            }
        }

        // Tour Dates Section
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Upcoming Tour Dates",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Spacer(modifier = Modifier.height(10.dp))

                artist.tourDates.forEach { tour ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "${tour.city} • ${tour.venue}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = textColor)
                                Text(text = tour.date, fontSize = 11.sp, color = NeonCyan)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (tour.ticketStatus == "Sold Out") Color(0xFFEF4444) else LiveGreen)
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(text = tour.ticketStatus, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Fan Club Comments & Moderation
                Text(
                    text = "Fan Club Wall & Comments",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newCommentText,
                        onValueChange = { newCommentText = it },
                        placeholder = { Text("Leave a message for ${artist.name}...", fontSize = 12.sp) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(20.dp),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (newCommentText.isNotBlank()) {
                                onAddComment("ARTIST", artist.id, newCommentText)
                                newCommentText = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text("Post", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }
        }

        items(artistComments) { comment ->
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                CommentItem(
                    comment = comment,
                    isDarkTheme = isDarkTheme,
                    onLike = { onToggleCommentLike(comment.id) },
                    onPin = { onPinComment(comment.id) },
                    onHide = { onHideComment(comment.id) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}
