package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.Playlist
import com.example.model.Track
import com.example.ui.theme.*

@Composable
fun PlaylistDetailScreen(
    playlist: Playlist,
    allTracks: List<Track>,
    isDarkTheme: Boolean,
    onBack: () -> Unit,
    onTrackClick: (Track) -> Unit,
    onRemoveTrack: (String) -> Unit,
    onMoveTrackUp: (Int) -> Unit,
    onMoveTrackDown: (Int) -> Unit,
    onEditPlaylistName: (String, String) -> Unit
) {
    val context = LocalContext.current
    val playlistTracks = playlist.trackIds.mapNotNull { id -> allTracks.find { it.id == id } }

    val bgColor = if (isDarkTheme) ObsidianBlack else PureWhite
    val textColor = if (isDarkTheme) TextWhitePrimary else TextDarkPrimary
    val textSub = if (isDarkTheme) TextWhiteSecondary else TextDarkSecondary

    var showEditDialog by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf(playlist.name) }
    var editDesc by remember { mutableStateOf(playlist.description) }

    if (showEditDialog) {
        Dialog(onDismissRequest = { showEditDialog = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = if (isDarkTheme) ObsidianCardElevated else PureWhiteCardElevated),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Edit Playlist Name",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Playlist Name", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = editDesc,
                        onValueChange = { editDesc = it },
                        label = { Text("Description", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showEditDialog = false }) {
                            Text("Cancel", color = textSub)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                onEditPlaylistName(editName, editDesc)
                                showEditDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                        ) {
                            Text("Save", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .padding(horizontal = 16.dp)
            .testTag("playlist_detail_screen")
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = textColor)
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Playlist Header Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF8B5CF6).copy(alpha = 0.5f), Color(0xFF1E1B4B))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = com.example.R.drawable.img_album_neon),
                    contentDescription = playlist.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(140.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = playlist.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = {
                        editName = playlist.name
                        editDesc = playlist.description
                        showEditDialog = true
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "Edit Playlist Name",
                        tint = NeonCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = playlist.description,
                fontSize = 13.sp,
                color = textSub
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Created by ${playlist.createdBy} • ${playlistTracks.size} tracks • Unlimited Playlists Enabled",
                fontSize = 12.sp,
                color = NeonCyan,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons: Share to Instagram / Copy Link
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "Listen to '${playlist.name}' on Vibelio 🎧:\nhttps://${playlist.shareCode}\n\nStream on Vibelio: https://ais-pre-2clzrw5lrvoc5hehrsg3ii-962702324750.asia-southeast1.run.app"
                            )
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Share to Instagram / Socials"))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE1306C)), // Instagram color
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Outlined.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Share to Instagram", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = {
                        val shareText = "https://${playlist.shareCode} (Vibelio App: https://ais-pre-2clzrw5lrvoc5hehrsg3ii-962702324750.asia-southeast1.run.app)"
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Playlist Link", shareText)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Playlist link copied to clipboard!", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Copy Share Link", fontSize = 12.sp, color = textColor)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Tracklist (Reorder & Manage)",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        if (playlistTracks.isEmpty()) {
            item {
                Text(
                    text = "No songs added yet. Add songs from Discover or search results!",
                    color = textSub,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(vertical = 20.dp)
                )
            }
        } else {
            itemsIndexed(playlistTracks) { index, track ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onTrackClick(track) }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "${index + 1}", color = textSub, fontSize = 12.sp, modifier = Modifier.width(24.dp))

                    Image(
                        painter = painterResource(id = track.coverRes),
                        contentDescription = track.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(46.dp)
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
                        Text(
                            text = track.artistName,
                            color = textSub,
                            fontSize = 12.sp
                        )
                    }

                    // Reorder Controls
                    IconButton(
                        onClick = { onMoveTrackUp(index) },
                        enabled = index > 0,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Move Up", tint = if (index > 0) textColor else textSub.copy(alpha = 0.3f))
                    }

                    IconButton(
                        onClick = { onMoveTrackDown(index) },
                        enabled = index < playlistTracks.size - 1,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Move Down", tint = if (index < playlistTracks.size - 1) textColor else textSub.copy(alpha = 0.3f))
                    }

                    IconButton(
                        onClick = { onRemoveTrack(track.id) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Outlined.Delete, contentDescription = "Remove", tint = ErrorRed, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}
