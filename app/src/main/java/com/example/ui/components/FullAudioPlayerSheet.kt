package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Comment
import com.example.model.Track
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullAudioPlayerSheet(
    track: Track?,
    isPlaying: Boolean,
    playbackPositionSec: Int,
    isShuffle: Boolean,
    isRepeat: Boolean,
    isDarkTheme: Boolean,
    isDownloaded: Boolean,
    comments: List<Comment>,
    userTierId: String,
    onDismiss: () -> Unit,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    onSeek: (Int) -> Unit,
    onToggleShuffle: () -> Unit,
    onToggleRepeat: () -> Unit,
    onToggleLike: (String) -> Unit,
    onToggleDownload: (String) -> Unit,
    onAddToPlaylist: (Track) -> Unit,
    onOpenArtist: (String) -> Unit,
    onAddComment: (String, String, String) -> Unit,
    onToggleCommentLike: (String) -> Unit,
    onPinComment: (String) -> Unit,
    onHideComment: (String) -> Unit,
    onUpgradeTier: () -> Unit
) {
    if (track == null) return

    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "vinyl_spin"
    )

    var selectedTab by remember { mutableStateOf(0) } // 0: Visualizer, 1: Real-time Lyrics, 2: Stems, 3: Comments
    var isLyricsOverlayMode by remember { mutableStateOf(false) }
    var newCommentText by remember { mutableStateOf("") }

    val bgColor = if (isDarkTheme) ObsidianBlack else PureWhite
    val textColor = if (isDarkTheme) TextWhitePrimary else TextDarkPrimary
    val textSub = if (isDarkTheme) TextWhiteSecondary else TextDarkSecondary

    // Calculate current active lyric line based on playback position
    val syncedLyrics = track.syncedLyrics
    val activeLyricIndex = remember(playbackPositionSec, syncedLyrics) {
        if (syncedLyrics.isEmpty()) -1
        else syncedLyrics.indexOfLast { it.timestampSec <= playbackPositionSec }.coerceAtLeast(0)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = bgColor,
        dragHandle = {
            BottomSheetDefaults.DragHandle(color = textSub.copy(alpha = 0.5f))
        },
        modifier = Modifier.fillMaxHeight(0.95f)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Main Player Scrollable Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Sheet Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Collapse",
                            tint = textColor,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "PLAYING FROM ALBUM",
                            color = textSub,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = track.albumName,
                            color = textColor,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row {
                        // Download Button (for offline listening with subscription)
                        IconButton(
                            onClick = { onToggleDownload(track.id) },
                            modifier = Modifier.testTag("download_track_btn")
                        ) {
                            Icon(
                                imageVector = if (isDownloaded) Icons.Default.FileDownloadDone else Icons.Outlined.FileDownload,
                                contentDescription = if (isDownloaded) "Downloaded" else "Download Offline",
                                tint = if (isDownloaded) LiveGreen else textColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        IconButton(onClick = { onAddToPlaylist(track) }) {
                            Icon(
                                imageVector = Icons.Outlined.PlaylistAdd,
                                contentDescription = "Add to playlist",
                                tint = textColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Vinyl / Album Art Display with subtle neon glow
                Box(
                    modifier = Modifier
                        .size(270.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.radialGradient(
                                listOf(NeonCyan.copy(alpha = 0.2f), Color.Transparent)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = track.coverRes),
                        contentDescription = track.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(250.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .then(if (isPlaying) Modifier.rotate(rotation * 0.05f) else Modifier)
                    )

                    if (track.isExclusive) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(14.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(NeonMagenta)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "VIP EXCLUSIVE",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (isDownloaded) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(14.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(LiveGreen)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "OFFLINE DOWNLOADED",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Track & Artist Title + Like Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = track.title,
                            color = textColor,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { onOpenArtist(track.artistId) }
                        ) {
                            Text(
                                text = track.artistName,
                                color = textSub,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Verified Indie",
                                tint = NeonCyan,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { onToggleLike(track.id) },
                            modifier = Modifier.testTag("full_player_like_btn")
                        ) {
                            Icon(
                                imageVector = if (track.isLiked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Like",
                                tint = if (track.isLiked) LiveGreen else textSub,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Progress Slider
                val progressFraction = (playbackPositionSec.toFloat() / track.durationSeconds.toFloat()).coerceIn(0f, 1f)

                Slider(
                    value = progressFraction,
                    onValueChange = { frac ->
                        onSeek((frac * track.durationSeconds).toInt())
                    },
                    colors = SliderDefaults.colors(
                        thumbColor = textColor,
                        activeTrackColor = LiveGreen,
                        inactiveTrackColor = textSub.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("playback_slider")
                )

                // Timestamps
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val currentMin = playbackPositionSec / 60
                    val currentSec = playbackPositionSec % 60
                    val totalMin = track.durationSeconds / 60
                    val totalSec = track.durationSeconds % 60

                    Text(
                        text = String.format("%d:%02d", currentMin, currentSec),
                        color = textSub,
                        fontSize = 12.sp
                    )
                    Text(
                        text = String.format("%d:%02d", totalMin, totalSec),
                        color = textSub,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Main Playback Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onToggleShuffle) {
                        Icon(
                            imageVector = Icons.Default.Shuffle,
                            contentDescription = "Shuffle",
                            tint = if (isShuffle) LiveGreen else textSub,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    IconButton(onClick = onPrev) {
                        Icon(
                            imageVector = Icons.Default.SkipPrevious,
                            contentDescription = "Previous",
                            tint = textColor,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    // Large Central Play/Pause
                    Box(
                        modifier = Modifier
                            .size(66.dp)
                            .clip(CircleShape)
                            .background(textColor)
                            .clickable { onPlayPause() }
                            .testTag("full_player_play_pause"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = bgColor,
                            modifier = Modifier.size(38.dp)
                        )
                    }

                    IconButton(onClick = onNext) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "Next",
                            tint = textColor,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    IconButton(onClick = onToggleRepeat) {
                        Icon(
                            imageVector = Icons.Default.Repeat,
                            contentDescription = "Repeat",
                            tint = if (isRepeat) LiveGreen else textSub,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Real-time Lyrics Quick Overlay Card (Spotify style lyrics preview card)
                if (syncedLyrics.isNotEmpty()) {
                    val currentLyricLine = syncedLyrics.getOrNull(activeLyricIndex)?.text ?: ""
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isDarkTheme) Color(0xFF1F1728) else Color(0xFFF3E8FF)
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isLyricsOverlayMode = true }
                            .testTag("synced_lyrics_preview_card")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Subtitles,
                                        contentDescription = null,
                                        tint = NeonMagenta,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "REAL-TIME LYRICS",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NeonMagenta,
                                        letterSpacing = 1.sp
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Full Overlay",
                                        fontSize = 11.sp,
                                        color = textSub,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Icon(
                                        imageVector = Icons.Default.OpenInFull,
                                        contentDescription = "Expand Lyrics",
                                        tint = textSub,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = currentLyricLine,
                                color = textColor,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 22.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                }

                // Feature Tabs (Visualizer, Synced Lyrics, Stems, Comments)
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = textColor,
                    divider = {}
                ) {
                    val tabTitles = listOf("Waveform", "Lyrics", "Stems", "Comments")
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tab Content
                when (selectedTab) {
                    0 -> {
                        // Audio Waveform Visualizer
                        Card(
                            colors = CardDefaults.cardColors(containerColor = if (isDarkTheme) ObsidianCard else PureWhiteCardElevated),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "REAL-TIME AUDIO FREQUENCY",
                                    color = textSub,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(60.dp),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    track.audioWaveform.forEachIndexed { idx, heightFactor ->
                                        val animatedBarHeight = if (isPlaying) {
                                            val animTransition = rememberInfiniteTransition(label = "bar_$idx")
                                            val dynamicScale by animTransition.animateFloat(
                                                initialValue = heightFactor * 0.4f,
                                                targetValue = heightFactor,
                                                animationSpec = infiniteRepeatable(
                                                    animation = tween(
                                                        durationMillis = 300 + (idx * 40) % 500,
                                                        easing = FastOutSlowInEasing
                                                    ),
                                                    repeatMode = RepeatMode.Reverse
                                                ),
                                                label = "bar_anim"
                                            )
                                            dynamicScale
                                        } else {
                                            heightFactor * 0.3f
                                        }

                                        Box(
                                            modifier = Modifier
                                                .width(5.dp)
                                                .fillMaxHeight(fraction = animatedBarHeight.coerceIn(0.1f, 1f))
                                                .clip(RoundedCornerShape(3.dp))
                                                .background(
                                                    Brush.verticalGradient(
                                                        listOf(NeonCyan, NeonMagenta)
                                                    )
                                                )
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "24-bit Lossless • 48 kHz • Indie Mastered",
                                    color = textSub,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                    1 -> {
                        // Synchronized Real-Time Lyrics View with line-by-line highlight & tap-to-seek
                        Card(
                            colors = CardDefaults.cardColors(containerColor = if (isDarkTheme) ObsidianCard else PureWhiteCardElevated),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Synchronized Lyrics",
                                        color = textColor,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    TextButton(onClick = { isLyricsOverlayMode = true }) {
                                        Text("Overlay Mode ↗", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Spacer(modifier = Modifier.height(10.dp))

                                if (syncedLyrics.isNotEmpty()) {
                                    SyncedLyricsList(
                                        lyrics = syncedLyrics,
                                        activeLyricIndex = activeLyricIndex,
                                        textColor = textColor,
                                        textSub = textSub,
                                        onLineClick = { timestamp -> onSeek(timestamp) }
                                    )
                                } else {
                                    Text(
                                        text = track.lyrics,
                                        color = textColor.copy(alpha = 0.85f),
                                        fontSize = 14.sp,
                                        lineHeight = 22.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Artist Notes: ${track.artistNotes}",
                                    color = textSub,
                                    fontSize = 12.sp,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                )
                            }
                        }
                    }
                    2 -> {
                        // Stems & Multi-tracks
                        Card(
                            colors = CardDefaults.cardColors(containerColor = if (isDarkTheme) ObsidianCard else PureWhiteCardElevated),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Multitrack Stems & Demos",
                                        color = textColor,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "PRODUCER TIER",
                                        color = NeonMagenta,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                val isUnlocked = userTierId == "tier_producer" || userTierId == "tier_backstage"
                                if (isUnlocked) {
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        StemRow("Vocal Stems (Dry & Wet)", "WAV 24-bit • 84 MB", textColor, textSub)
                                        StemRow("Drum Machine & Percussion", "WAV 24-bit • 62 MB", textColor, textSub)
                                        StemRow("Analog Synths & Bass MIDI", "MIDI + WAV • 45 MB", textColor, textSub)
                                    }
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(NeonMagenta.copy(alpha = 0.12f))
                                            .padding(14.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = "🔒 Locked for Subscribers",
                                                color = textColor,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "Unlock individual stems, remixes, and 24-bit acoustic tracks.",
                                                color = textSub,
                                                fontSize = 12.sp,
                                                textAlign = TextAlign.Center
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Button(
                                                onClick = onUpgradeTier,
                                                colors = ButtonDefaults.buttonColors(containerColor = NeonMagenta),
                                                shape = RoundedCornerShape(20.dp)
                                            ) {
                                                Text("Unlock VIP Pass (₹65/mo)", fontSize = 12.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    3 -> {
                        // Track Comments & Reactions
                        val trackComments = comments.filter { it.targetType == "SONG" && it.targetId == track.id }
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Post comment input
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = newCommentText,
                                    onValueChange = { newCommentText = it },
                                    placeholder = { Text("Leave a comment on this song...", fontSize = 12.sp) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("comment_input"),
                                    shape = RoundedCornerShape(24.dp),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = NeonCyan,
                                        unfocusedBorderColor = textSub.copy(alpha = 0.4f)
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        if (newCommentText.isNotBlank()) {
                                            onAddComment("SONG", track.id, newCommentText)
                                            newCommentText = ""
                                        }
                                    },
                                    shape = RoundedCornerShape(20.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                                ) {
                                    Text("Send", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }

                            if (trackComments.isEmpty()) {
                                Text(
                                    text = "No comments yet. Be the first fan to react!",
                                    color = textSub,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(vertical = 12.dp)
                                )
                            } else {
                                trackComments.forEach { comment ->
                                    CommentItem(
                                        comment = comment,
                                        isDarkTheme = isDarkTheme,
                                        onLike = { onToggleCommentLike(comment.id) },
                                        onPin = { onPinComment(comment.id) },
                                        onHide = { onHideComment(comment.id) }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }

            // Real-Time Lyrics Full Text Overlay View
            androidx.compose.animation.AnimatedVisibility(
                visible = isLyricsOverlayMode,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { it })
            ) {
                RealTimeLyricsOverlayView(
                    track = track,
                    syncedLyrics = syncedLyrics,
                    activeLyricIndex = activeLyricIndex,
                    playbackPositionSec = playbackPositionSec,
                    isPlaying = isPlaying,
                    onClose = { isLyricsOverlayMode = false },
                    onSeek = onSeek,
                    onPlayPause = onPlayPause
                )
            }
        }
    }
}

@Composable
fun SyncedLyricsList(
    lyrics: List<com.example.model.SyncedLyricLine>,
    activeLyricIndex: Int,
    textColor: Color,
    textSub: Color,
    onLineClick: (Int) -> Unit
) {
    val listState = rememberLazyListState()

    LaunchedEffect(activeLyricIndex) {
        if (activeLyricIndex >= 0 && activeLyricIndex < lyrics.size) {
            listState.animateScrollToItem((activeLyricIndex - 1).coerceAtLeast(0))
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        itemsIndexed(lyrics) { index, line ->
            val isActive = index == activeLyricIndex
            val isPast = index < activeLyricIndex

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isActive) NeonCyan.copy(alpha = 0.15f) else Color.Transparent)
                    .clickable { onLineClick(line.timestampSec) }
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = String.format("%d:%02d", line.timestampSec / 60, line.timestampSec % 60),
                    fontSize = 11.sp,
                    color = if (isActive) NeonCyan else textSub.copy(alpha = 0.5f),
                    modifier = Modifier.width(36.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = line.text,
                    fontSize = if (isActive) 17.sp else 14.sp,
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                    color = when {
                        isActive -> textColor
                        isPast -> textSub.copy(alpha = 0.7f)
                        else -> textSub.copy(alpha = 0.4f)
                    },
                    lineHeight = 22.sp
                )
            }
        }
    }
}

@Composable
fun RealTimeLyricsOverlayView(
    track: Track,
    syncedLyrics: List<com.example.model.SyncedLyricLine>,
    activeLyricIndex: Int,
    playbackPositionSec: Int,
    isPlaying: Boolean,
    onClose: () -> Unit,
    onSeek: (Int) -> Unit,
    onPlayPause: () -> Unit
) {
    val listState = rememberLazyListState()

    LaunchedEffect(activeLyricIndex) {
        if (activeLyricIndex >= 0 && activeLyricIndex < syncedLyrics.size) {
            listState.animateScrollToItem((activeLyricIndex - 2).coerceAtLeast(0))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0F081E).copy(alpha = 0.98f),
                        Color(0xFF05030A).copy(alpha = 0.99f)
                    )
                )
            )
            .padding(horizontal = 24.dp)
            .testTag("real_time_lyrics_overlay_view")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(12.dp))

            // Overlay Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Lyrics Overlay",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "REAL-TIME LYRICS",
                        color = NeonCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "${track.title} • ${track.artistName}",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(onClick = onPlayPause) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = LiveGreen,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Main Lyrics Synchronized Display
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(22.dp),
                contentPadding = PaddingValues(vertical = 40.dp)
            ) {
                itemsIndexed(syncedLyrics) { index, line ->
                    val isActive = index == activeLyricIndex
                    val isPast = index < activeLyricIndex

                    Text(
                        text = line.text,
                        fontSize = if (isActive) 24.sp else 18.sp,
                        fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.SemiBold,
                        color = when {
                            isActive -> Color.White
                            isPast -> Color.White.copy(alpha = 0.5f)
                            else -> Color.White.copy(alpha = 0.25f)
                        },
                        lineHeight = if (isActive) 32.sp else 26.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSeek(line.timestampSec) }
                            .padding(vertical = 4.dp)
                    )
                }
            }

            // Bottom Progress & Seek Notice
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val currentMin = playbackPositionSec / 60
                val currentSec = playbackPositionSec % 60
                Text(
                    text = String.format("Current Time: %d:%02d", currentMin, currentSec),
                    color = NeonCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Tap any line to seek audio",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun StemRow(name: String, detail: String, textColor: Color, textSub: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(textColor.copy(alpha = 0.05f))
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = name, color = textColor, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Text(text = detail, color = textSub, fontSize = 11.sp)
        }
        Icon(
            imageVector = Icons.Default.Download,
            contentDescription = "Download stem",
            tint = NeonCyan,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun CommentItem(
    comment: Comment,
    isDarkTheme: Boolean,
    onLike: () -> Unit,
    onPin: () -> Unit,
    onHide: () -> Unit
) {
    val cardBg = if (isDarkTheme) ObsidianCard else PureWhiteCardElevated
    val textColor = if (isDarkTheme) TextWhitePrimary else TextDarkPrimary
    val textSub = if (isDarkTheme) TextWhiteSecondary else TextDarkSecondary

    Card(
        colors = CardDefaults.cardColors(containerColor = cardBg),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = comment.authorName,
                        color = textColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    comment.authorBadge?.let { badge ->
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(NeonMagenta.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = badge,
                                color = NeonMagenta,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    if (comment.isPinned) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.PushPin,
                            contentDescription = "Pinned",
                            tint = AccentGold,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
                Text(text = comment.timestamp, color = textSub, fontSize = 11.sp)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(text = comment.text, color = textColor, fontSize = 13.sp)

            comment.artistReply?.let { reply ->
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(NeonCyan.copy(alpha = 0.1f))
                        .padding(8.dp)
                ) {
                    Text(
                        text = "⭐ Artist: $reply",
                        color = NeonCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Actions: Like, Pin, Hide
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onLike() }
                ) {
                    Icon(
                        imageVector = if (comment.isLiked) Icons.Default.ThumbUp else Icons.Outlined.ThumbUp,
                        contentDescription = "Like",
                        tint = if (comment.isLiked) LiveGreen else textSub,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${comment.likes}", color = textSub, fontSize = 11.sp)
                }

                Row {
                    IconButton(onClick = onPin, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.PushPin,
                            contentDescription = "Pin comment",
                            tint = if (comment.isPinned) AccentGold else textSub,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    IconButton(onClick = onHide, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.VisibilityOff,
                            contentDescription = "Hide comment",
                            tint = textSub,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}
