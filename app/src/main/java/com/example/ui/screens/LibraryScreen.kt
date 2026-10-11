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
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.SwapVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
fun LibraryScreen(
    playlists: List<Playlist>,
    artists: List<Artist>,
    savedTrackIds: Set<String>,
    downloadedTrackIds: Set<String>,
    allTracks: List<Track>,
    isDarkTheme: Boolean,
    onPlaylistClick: (String) -> Unit,
    onArtistClick: (String) -> Unit,
    onCreatePlaylistClick: () -> Unit,
    onYouTubeImportClick: () -> Unit,
    onTrackClick: (Track) -> Unit
) {
    var selectedFilter by remember { mutableStateOf("Playlists") } // "Playlists", "Artists", "Downloaded"

    val bgColor = if (isDarkTheme) ObsidianBlack else PureWhite
    val textColor = if (isDarkTheme) TextWhitePrimary else TextDarkPrimary
    val textSub = if (isDarkTheme) TextWhiteSecondary else TextDarkSecondary

    val likedTracksCount = savedTrackIds.size

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .padding(horizontal = 16.dp)
            .testTag("library_screen")
    ) {
        // Top Filter Pills matching user's screenshot
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    val filterPills = listOf("Playlists", "Artists", "Downloaded")
                    filterPills.forEach { filter ->
                        val isSelected = selectedFilter == filter
                        val pillBg = if (isSelected) (if (isDarkTheme) Color(0xFF2A2A2A) else Color(0xFFE5E7EB)) else Color.Transparent
                        Box(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(pillBg)
                                .clickable { selectedFilter = filter }
                                .padding(horizontal = 12.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = filter,
                                color = textColor,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }

                // Add / Import Actions
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onYouTubeImportClick,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Link,
                            contentDescription = "Import from YouTube",
                            tint = Color(0xFFFF0000),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    IconButton(
                        onClick = onCreatePlaylistClick,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Create Playlist",
                            tint = textColor,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Subheader: "Recents" with sort icon and grid view icon
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { /* sort toggler */ }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.SwapVert,
                        contentDescription = "Sort",
                        tint = textColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Recents",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = textColor
                    )
                }

                Icon(
                    imageVector = Icons.Outlined.GridView,
                    contentDescription = "View Style",
                    tint = textSub,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Prominent Liked Songs Tile (matching screenshot 3)
        if (selectedFilter == "Playlists" || selectedFilter == "All") {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .clickable {
                            val firstLiked = allTracks.find { savedTrackIds.contains(it.id) }
                            if (firstLiked != null) onTrackClick(firstLiked)
                        }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Purple gradient box with white heart
                    Box(
                        modifier = Modifier
                            .size(62.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF450AF5), Color(0xFFC4EFD9))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Liked",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Liked Songs",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = textColor
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PushPin,
                                contentDescription = "Pinned",
                                tint = LiveGreen,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Playlist • $likedTracksCount songs",
                                fontSize = 12.sp,
                                color = textSub
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            // Custom Playlists (matching screenshot 3 layout)
            items(playlists) { playlist ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { onPlaylistClick(playlist.id) }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 4-grid playlist artwork representation
                    Box(
                        modifier = Modifier
                            .size(62.dp)
                            .clip(RoundedCornerShape(4.dp))
                    ) {
                        Image(
                            painter = painterResource(id = com.example.R.drawable.img_album_neon),
                            contentDescription = playlist.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = playlist.name,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = textColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PushPin,
                                contentDescription = "Pinned",
                                tint = LiveGreen,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Playlist • ${playlist.createdBy} (${playlist.trackIds.size} songs)",
                                fontSize = 12.sp,
                                color = textSub,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // Circular Artist Avatars (matching screenshot 3)
        if (selectedFilter == "Artists" || selectedFilter == "All") {
            items(artists) { artist ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { onArtistClick(artist.id) }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(62.dp)
                            .clip(CircleShape)
                    ) {
                        Image(
                            painter = painterResource(id = artist.avatarRes),
                            contentDescription = artist.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = artist.name,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = textColor
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Artist • ${artist.followersCount / 1000}k followers",
                            fontSize = 12.sp,
                            color = textSub
                        )
                    }
                }
            }
        }

        // Downloaded Offline Songs (Unlocked with ₹65 subscription)
        if (selectedFilter == "Downloaded") {
            val downloadedTracks = allTracks.filter { downloadedTrackIds.contains(it.id) }
            if (downloadedTracks.isEmpty()) {
                item {
                    Text(
                        text = "No songs downloaded yet. Open any song in the player and tap the download icon to save it for offline listening!",
                        color = textSub,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(vertical = 24.dp)
                    )
                }
            } else {
                items(downloadedTracks) { track ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { onTrackClick(track) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = track.coverRes),
                            contentDescription = track.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(6.dp))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = track.title,
                                color = textColor,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.FileDownloadDone,
                                    contentDescription = "Downloaded",
                                    tint = LiveGreen,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${track.artistName} • Offline 24-bit",
                                    color = textSub,
                                    fontSize = 11.sp
                                )
                            }
                        }
                        IconButton(onClick = { onTrackClick(track) }) {
                            Icon(Icons.Default.PlayCircle, contentDescription = "Play", tint = NeonCyan, modifier = Modifier.size(28.dp))
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
