package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.model.Artist
import com.example.model.Playlist
import com.example.model.Track
import com.example.ui.theme.*

@Composable
fun DiscoverScreen(
    tracks: List<Track>,
    artists: List<Artist>,
    playlists: List<Playlist>,
    selectedChip: String,
    isDarkTheme: Boolean,
    unreadNotifCount: Int,
    onChipSelect: (String) -> Unit,
    onTrackClick: (Track) -> Unit,
    onArtistClick: (String) -> Unit,
    onPlaylistClick: (String) -> Unit,
    onToggleTheme: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenYouTubeImport: () -> Unit,
    onShareApp: () -> Unit = {}
) {
    val bgColor = if (isDarkTheme) ObsidianBlack else PureWhite
    val textColor = if (isDarkTheme) TextWhitePrimary else TextDarkPrimary
    val textSub = if (isDarkTheme) TextWhiteSecondary else TextDarkSecondary

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .padding(horizontal = 16.dp)
            .testTag("discover_screen")
    ) {
        // Top App Bar matching user's screenshot
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Profile Avatar Circle ("K")
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE65100))
                            .testTag("avatar_profile_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "K",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Filter Chips: All, Music (Podcast removed)
                    val chips = listOf("All", "Music")
                    chips.forEach { chip ->
                        val isSelected = selectedChip == chip
                        val chipBg = if (isSelected) LiveGreen else (if (isDarkTheme) Color(0xFF2A2A2A) else Color(0xFFE5E7EB))
                        val chipText = if (isSelected) Color.Black else textColor

                        Box(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(chipBg)
                                .clickable { onChipSelect(chip) }
                                .padding(horizontal = 14.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = chip,
                                color = chipText,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }

                // Action Icons: Share App, Theme Switcher & Notifications Bell
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onShareApp,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("action_share_app")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share Vibelio App",
                            tint = LiveGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(onClick = onToggleTheme, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Outlined.LightMode else Icons.Outlined.DarkMode,
                            contentDescription = "Toggle White/Black Theme",
                            tint = textColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Box {
                        IconButton(onClick = onOpenNotifications, modifier = Modifier.size(36.dp)) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = "Alerts",
                                tint = textColor,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        if (unreadNotifCount > 0) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(NeonMagenta)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Section 1: "Recents" (matching screenshot 1)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recents",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Text(
                    text = "Show all",
                    fontSize = 12.sp,
                    color = textSub,
                    modifier = Modifier.clickable { /* show all */ }
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(end = 16.dp)
            ) {
                // Playlists & Singles in Recents
                items(playlists) { playlist ->
                    RecentCard(
                        title = playlist.name,
                        subtitle = "Playlist • ${playlist.createdBy}",
                        badge = "${playlist.trackIds.size} songs",
                        coverRes = com.example.R.drawable.img_album_neon,
                        textColor = textColor,
                        textSub = textSub,
                        onClick = { onPlaylistClick(playlist.id) }
                    )
                }

                items(tracks.take(3)) { track ->
                    RecentCard(
                        title = track.title,
                        subtitle = "Single • ${track.artistName}",
                        badge = if (track.isLiked) "Liked" else null,
                        coverRes = track.coverRes,
                        textColor = textColor,
                        textSub = textSub,
                        onClick = { onTrackClick(track) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(26.dp))
        }

        // Section 2: "Albums featuring songs you like"
        item {
            Text(
                text = "Albums featuring songs you like",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Spacer(modifier = Modifier.height(14.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(end = 16.dp)
            ) {
                items(tracks) { track ->
                    AlbumFeatureCard(
                        track = track,
                        textColor = textColor,
                        textSub = textSub,
                        onClick = { onTrackClick(track) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(28.dp))
        }

        // Section 3: "Recommended for today"
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recommended for today",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Text(
                    text = "Personalized",
                    fontSize = 11.sp,
                    color = NeonCyan,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(14.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(end = 16.dp)
            ) {
                items(tracks.reversed()) { track ->
                    AlbumFeatureCard(
                        track = track,
                        textColor = textColor,
                        textSub = textSub,
                        onClick = { onTrackClick(track) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(28.dp))
        }

        // Section 4: "New releases from followed artists"
        item {
            Text(
                text = "New releases from followed artists",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Spacer(modifier = Modifier.height(14.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                artists.forEach { artist ->
                    val latestTrack = tracks.find { it.artistId == artist.id }
                    if (latestTrack != null) {
                        FollowedReleaseRow(
                            artist = artist,
                            track = latestTrack,
                            textColor = textColor,
                            textSub = textSub,
                            onTrackClick = { onTrackClick(latestTrack) },
                            onArtistClick = { onArtistClick(artist.id) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(120.dp)) // padding for bottom bar & mini player
        }
    }
}

@Composable
fun RecentCard(
    title: String,
    subtitle: String,
    badge: String?,
    coverRes: Int,
    textColor: Color,
    textSub: Color,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(118.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(118.dp)
                .clip(RoundedCornerShape(8.dp))
        ) {
            Image(
                painter = painterResource(id = coverRes),
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            color = textColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = subtitle,
            color = textSub,
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (badge != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = LiveGreen,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = badge, color = textSub, fontSize = 10.sp)
            }
        }
    }
}

@Composable
fun AlbumFeatureCard(
    track: Track,
    textColor: Color,
    textSub: Color,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(150.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(150.dp)
                .clip(RoundedCornerShape(8.dp))
        ) {
            Image(
                painter = painterResource(id = track.coverRes),
                contentDescription = track.albumName,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            if (track.isExclusive) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(NeonMagenta)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = "VIP", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = track.albumName,
            color = textColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = track.artistName,
            color = textSub,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun FollowedReleaseRow(
    artist: Artist,
    track: Track,
    textColor: Color,
    textSub: Color,
    onTrackClick: () -> Unit,
    onArtistClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { onTrackClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(6.dp))
        ) {
            Image(
                painter = painterResource(id = track.coverRes),
                contentDescription = track.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = track.title,
                color = textColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${artist.name} • ${track.releaseDate}",
                    color = textSub,
                    fontSize = 11.sp
                )
            }
        }

        IconButton(onClick = onTrackClick) {
            Icon(
                imageVector = Icons.Default.PlayCircle,
                contentDescription = "Play",
                tint = NeonCyan,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}
