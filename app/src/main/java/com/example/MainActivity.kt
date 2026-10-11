package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.Track
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.viewmodel.AppTab
import com.example.viewmodel.TuneZxViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: TuneZxViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            var showShareAppDialog by remember { mutableStateOf(false) }
            var isNotificationsScreenOpen by remember { mutableStateOf(false) }

            // Back handler to navigate back cleanly from sub-screens
            BackHandler(
                enabled = uiState.activeTrackDetailId != null ||
                          uiState.activeArtistDetailId != null ||
                          uiState.activePlaylistDetailId != null ||
                          uiState.activeConversationId != null ||
                          isNotificationsScreenOpen ||
                          showShareAppDialog
            ) {
                if (showShareAppDialog) {
                    showShareAppDialog = false
                } else if (isNotificationsScreenOpen) {
                    isNotificationsScreenOpen = false
                } else {
                    viewModel.closeSubScreens()
                }
            }

            TuneZxTheme(darkTheme = uiState.isDarkTheme) {
                val navBarBg = if (uiState.isDarkTheme) ObsidianBlack else PureWhite
                val textColor = if (uiState.isDarkTheme) TextWhitePrimary else TextDarkPrimary
                val textSub = if (uiState.isDarkTheme) TextWhiteSecondary else TextDarkSecondary

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = if (uiState.isDarkTheme) ObsidianBlack else PureWhite,
                    topBar = {
                        // Push alert notification banner (slides in smoothly when triggered)
                        PushNotificationBanner(
                            notification = uiState.activePushBanner,
                            onDismiss = { viewModel.dismissPushBanner() },
                            onClick = { notif ->
                                viewModel.dismissPushBanner()
                                isNotificationsScreenOpen = true
                            },
                            modifier = Modifier.statusBarsPadding()
                        )
                    },
                    bottomBar = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .navigationBarsPadding()
                                .background(navBarBg)
                        ) {
                            // Persistent Mini Player Bar
                            MiniPlayerBar(
                                track = uiState.currentTrack,
                                isPlaying = uiState.isPlaying,
                                playbackPositionSec = uiState.playbackPositionSec,
                                isDarkTheme = uiState.isDarkTheme,
                                onBarClick = { viewModel.setFullPlayerVisible(true) },
                                onPlayPauseClick = { viewModel.togglePlayPause() },
                                onLikeClick = {
                                    uiState.currentTrack?.let { viewModel.toggleLikeTrack(it.id) }
                                }
                            )

                            // 5-Tab Modern Bottom Navigation Bar
                            NavigationBar(
                                containerColor = navBarBg,
                                tonalElevation = 0.dp
                            ) {
                                NavigationBarItem(
                                    selected = uiState.currentTab == AppTab.HOME && !isNotificationsScreenOpen && uiState.activeConversationId == null && uiState.activePlaylistDetailId == null && uiState.activeArtistDetailId == null,
                                    onClick = {
                                        isNotificationsScreenOpen = false
                                        viewModel.selectTab(AppTab.HOME)
                                    },
                                    icon = {
                                        Icon(
                                            imageVector = if (uiState.currentTab == AppTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                                            contentDescription = "Home"
                                        )
                                    },
                                    label = { Text("Home", fontSize = 10.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = textColor,
                                        selectedTextColor = textColor,
                                        unselectedIconColor = textSub,
                                        unselectedTextColor = textSub,
                                        indicatorColor = Color.Transparent
                                    ),
                                    modifier = Modifier.testTag("nav_home")
                                )

                                NavigationBarItem(
                                    selected = uiState.currentTab == AppTab.SEARCH,
                                    onClick = {
                                        isNotificationsScreenOpen = false
                                        viewModel.selectTab(AppTab.SEARCH)
                                    },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.Search,
                                            contentDescription = "Search"
                                        )
                                    },
                                    label = { Text("Search", fontSize = 10.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = textColor,
                                        selectedTextColor = textColor,
                                        unselectedIconColor = textSub,
                                        unselectedTextColor = textSub,
                                        indicatorColor = Color.Transparent
                                    ),
                                    modifier = Modifier.testTag("nav_search")
                                )

                                NavigationBarItem(
                                    selected = uiState.currentTab == AppTab.LIBRARY,
                                    onClick = {
                                        isNotificationsScreenOpen = false
                                        viewModel.selectTab(AppTab.LIBRARY)
                                    },
                                    icon = {
                                        Icon(
                                            imageVector = if (uiState.currentTab == AppTab.LIBRARY) Icons.Filled.LibraryMusic else Icons.Outlined.LibraryMusic,
                                            contentDescription = "Your Library"
                                        )
                                    },
                                    label = { Text("Library", fontSize = 10.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = textColor,
                                        selectedTextColor = textColor,
                                        unselectedIconColor = textSub,
                                        unselectedTextColor = textSub,
                                        indicatorColor = Color.Transparent
                                    ),
                                    modifier = Modifier.testTag("nav_library")
                                )

                                NavigationBarItem(
                                    selected = uiState.currentTab == AppTab.ANALYTICS,
                                    onClick = {
                                        isNotificationsScreenOpen = false
                                        viewModel.selectTab(AppTab.ANALYTICS)
                                    },
                                    icon = {
                                        Icon(
                                            imageVector = if (uiState.currentTab == AppTab.ANALYTICS) Icons.Filled.Leaderboard else Icons.Outlined.Leaderboard,
                                            contentDescription = "Analytics & VIP"
                                        )
                                    },
                                    label = { Text("Analytics", fontSize = 10.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = textColor,
                                        selectedTextColor = textColor,
                                        unselectedIconColor = textSub,
                                        unselectedTextColor = textSub,
                                        indicatorColor = Color.Transparent
                                    ),
                                    modifier = Modifier.testTag("nav_analytics")
                                )

                                NavigationBarItem(
                                    selected = uiState.currentTab == AppTab.MESSAGES || uiState.currentTab == AppTab.COMMUNITY,
                                    onClick = {
                                        isNotificationsScreenOpen = false
                                        viewModel.selectTab(AppTab.MESSAGES)
                                    },
                                    icon = {
                                        Icon(
                                            imageVector = if (uiState.currentTab == AppTab.MESSAGES) Icons.Filled.Forum else Icons.Outlined.Forum,
                                            contentDescription = "Social & Chat"
                                        )
                                    },
                                    label = { Text("Social", fontSize = 10.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = textColor,
                                        selectedTextColor = textColor,
                                        unselectedIconColor = textSub,
                                        unselectedTextColor = textSub,
                                        indicatorColor = Color.Transparent
                                    ),
                                    modifier = Modifier.testTag("nav_messages")
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        // Main Screen Switching
                        when {
                            isNotificationsScreenOpen -> {
                                NotificationsScreen(
                                    notifications = uiState.notifications,
                                    isDarkTheme = uiState.isDarkTheme,
                                    onBack = { isNotificationsScreenOpen = false },
                                    onMarkAllRead = { viewModel.markAllNotificationsRead() },
                                    onNotificationClick = { notif ->
                                        isNotificationsScreenOpen = false
                                        notif.relatedId?.let { relId ->
                                            val targetTrack = uiState.tracks.find { it.id == relId }
                                            if (targetTrack != null) viewModel.playTrack(targetTrack)
                                            else viewModel.openArtistDetail(relId)
                                        }
                                    }
                                )
                            }

                            uiState.activeConversationId != null -> {
                                val conv = uiState.conversations.find { it.id == uiState.activeConversationId }
                                if (conv != null) {
                                    ConversationDetailScreen(
                                        conversation = conv,
                                        isDarkTheme = uiState.isDarkTheme,
                                        onBack = { viewModel.closeSubScreens() },
                                        onSendMessage = { text -> viewModel.sendMessage(conv.id, text) }
                                    )
                                }
                            }

                            uiState.activePlaylistDetailId != null -> {
                                val pl = uiState.playlists.find { it.id == uiState.activePlaylistDetailId }
                                if (pl != null) {
                                    PlaylistDetailScreen(
                                        playlist = pl,
                                        allTracks = uiState.tracks,
                                        isDarkTheme = uiState.isDarkTheme,
                                        onBack = { viewModel.closeSubScreens() },
                                        onTrackClick = { viewModel.playTrack(it) },
                                        onRemoveTrack = { trackId -> viewModel.removeTrackFromPlaylist(pl.id, trackId) },
                                        onMoveTrackUp = { idx -> viewModel.reorderPlaylistTrack(pl.id, idx, idx - 1) },
                                        onMoveTrackDown = { idx -> viewModel.reorderPlaylistTrack(pl.id, idx, idx + 1) },
                                        onEditPlaylistName = { newName, newDesc -> viewModel.updatePlaylistName(pl.id, newName, newDesc) }
                                    )
                                }
                            }

                            uiState.activeArtistDetailId != null -> {
                                val artist = uiState.artists.find { it.id == uiState.activeArtistDetailId }
                                if (artist != null) {
                                    ArtistProfileScreen(
                                        artist = artist,
                                        tracks = uiState.tracks,
                                        comments = uiState.comments,
                                        isFollowed = uiState.followedArtistIds.contains(artist.id),
                                        userTierId = uiState.userSubscribedTierId,
                                        isDarkTheme = uiState.isDarkTheme,
                                        onBack = { viewModel.closeSubScreens() },
                                        onToggleFollow = { viewModel.toggleFollowArtist(artist.id) },
                                        onTrackClick = { viewModel.playTrack(it) },
                                        onPromptSubscribe = { tier -> viewModel.promptSubscribe(tier) },
                                        onAddComment = { targetType, targetId, text -> viewModel.addComment(targetType, targetId, text) },
                                        onToggleCommentLike = { id -> viewModel.toggleCommentLike(id) },
                                        onPinComment = { id -> viewModel.togglePinComment(id) },
                                        onHideComment = { id -> viewModel.toggleHideComment(id) }
                                    )
                                }
                            }

                            else -> {
                                when (uiState.currentTab) {
                                    AppTab.HOME -> {
                                        DiscoverScreen(
                                            tracks = uiState.tracks,
                                            artists = uiState.artists,
                                            playlists = uiState.playlists,
                                            selectedChip = uiState.selectedHomeChip,
                                            isDarkTheme = uiState.isDarkTheme,
                                            unreadNotifCount = uiState.notifications.count { !it.isRead },
                                            onChipSelect = { viewModel.selectHomeChip(it) },
                                            onTrackClick = { viewModel.playTrack(it) },
                                            onArtistClick = { viewModel.openArtistDetail(it) },
                                            onPlaylistClick = { viewModel.openPlaylistDetail(it) },
                                            onToggleTheme = { viewModel.toggleTheme() },
                                            onOpenNotifications = { isNotificationsScreenOpen = true },
                                            onOpenYouTubeImport = { viewModel.openYouTubeImportDialog() },
                                            onShareApp = { showShareAppDialog = true }
                                        )
                                    }

                                    AppTab.SEARCH -> {
                                        SearchScreen(
                                            searchQuery = uiState.searchQuery,
                                            tracks = uiState.tracks,
                                            isDarkTheme = uiState.isDarkTheme,
                                            onQueryChange = { viewModel.setSearchQuery(it) },
                                            onTrackClick = { viewModel.playTrack(it) }
                                        )
                                    }

                                    AppTab.LIBRARY -> {
                                        LibraryScreen(
                                            playlists = uiState.playlists,
                                            artists = uiState.artists,
                                            savedTrackIds = uiState.userSavedTrackIds,
                                            downloadedTrackIds = uiState.downloadedTrackIds,
                                            allTracks = uiState.tracks,
                                            isDarkTheme = uiState.isDarkTheme,
                                            onPlaylistClick = { viewModel.openPlaylistDetail(it) },
                                            onArtistClick = { viewModel.openArtistDetail(it) },
                                            onCreatePlaylistClick = { viewModel.openCreatePlaylistDialog() },
                                            onYouTubeImportClick = { viewModel.openYouTubeImportDialog() },
                                            onTrackClick = { viewModel.playTrack(it) }
                                        )
                                    }

                                    AppTab.ANALYTICS -> {
                                        val tierList = com.example.data.TuneZxSampleData.sampleSubscriptionTiers
                                        ArtistAnalyticsScreen(
                                            demographics = uiState.demographics,
                                            subscriptionTiers = tierList,
                                            currentSubscribedTierId = uiState.userSubscribedTierId,
                                            timeRange = uiState.analyticsTimeRange,
                                            isDarkTheme = uiState.isDarkTheme,
                                            onSelectTimeRange = { viewModel.setAnalyticsTimeRange(it) },
                                            onPromptSubscribe = { tier -> viewModel.promptSubscribe(tier) },
                                            onRequestPayout = { viewModel.requestInstantPayout() }
                                        )
                                    }

                                    AppTab.COMMUNITY, AppTab.MESSAGES -> {
                                        SocialAndMessagesScreen(
                                            conversations = uiState.conversations,
                                            communityPosts = uiState.communityPosts,
                                            profanityFilterEnabled = uiState.profanityFilterEnabled,
                                            isDarkTheme = uiState.isDarkTheme,
                                            onOpenConversation = { viewModel.openConversation(it) },
                                            onPostLike = { viewModel.togglePostLike(it) },
                                            onToggleProfanityFilter = { viewModel.toggleProfanityFilter() },
                                            onTriggerReleaseAlert = { viewModel.triggerSimulatedReleaseAlert() },
                                            onTriggerTourAlert = { viewModel.triggerSimulatedTourAlert() }
                                        )
                                    }
                                }
                            }
                        }

                        // Full Screen Audio Player Sheet
                        if (uiState.isFullPlayerVisible) {
                            FullAudioPlayerSheet(
                                track = uiState.currentTrack,
                                isPlaying = uiState.isPlaying,
                                playbackPositionSec = uiState.playbackPositionSec,
                                isShuffle = uiState.isShuffle,
                                isRepeat = uiState.isRepeat,
                                isDarkTheme = uiState.isDarkTheme,
                                isDownloaded = uiState.downloadedTrackIds.contains(uiState.currentTrack?.id),
                                comments = uiState.comments,
                                userTierId = uiState.userSubscribedTierId,
                                onDismiss = { viewModel.setFullPlayerVisible(false) },
                                onPlayPause = { viewModel.togglePlayPause() },
                                onNext = { viewModel.playNext() },
                                onPrev = { viewModel.playPrev() },
                                onSeek = { viewModel.seekTo(it) },
                                onToggleShuffle = { viewModel.toggleShuffle() },
                                onToggleRepeat = { viewModel.toggleRepeat() },
                                onToggleLike = { id -> viewModel.toggleLikeTrack(id) },
                                onToggleDownload = { id -> viewModel.toggleDownloadTrack(id) },
                                onAddToPlaylist = { track -> viewModel.openAddToPlaylistSheet(track) },
                                onOpenArtist = { artistId ->
                                    viewModel.setFullPlayerVisible(false)
                                    viewModel.openArtistDetail(artistId)
                                },
                                onAddComment = { targetType, targetId, text -> viewModel.addComment(targetType, targetId, text) },
                                onToggleCommentLike = { id -> viewModel.toggleCommentLike(id) },
                                onPinComment = { id -> viewModel.togglePinComment(id) },
                                onHideComment = { id -> viewModel.toggleHideComment(id) },
                                onUpgradeTier = {
                                    val tier = com.example.data.TuneZxSampleData.sampleSubscriptionTiers.find { it.id == "tier_backstage" }
                                    if (tier != null) viewModel.promptSubscribe(tier)
                                }
                            )
                        }

                        // Ad Interstitial Break Dialog (Plays every 4 songs on free tier)
                        if (uiState.showAdOverlay) {
                            AdInterstitialDialog(
                                songCount = uiState.songsPlayedCount,
                                isDarkTheme = uiState.isDarkTheme,
                                onDismiss = { viewModel.dismissAd() },
                                onGetSubscription = {
                                    val tier = com.example.data.TuneZxSampleData.sampleSubscriptionTiers.find { it.id == "tier_backstage" }
                                    if (tier != null) viewModel.promptSubscribe(tier)
                                }
                            )
                        }

                        // Universal Payment Gateway Dialog (Supports any payment app: GPay, PhonePe, Paytm, UPI, Card)
                        uiState.showPaymentModalForTier?.let { tier ->
                            PaymentGatewayDialog(
                                tier = tier,
                                isDarkTheme = uiState.isDarkTheme,
                                onDismiss = { viewModel.dismissPaymentModal() },
                                onPaymentSuccess = { tierId ->
                                    viewModel.completeSubscription(tierId)
                                }
                            )
                        }

                        // Create Unlimited Playlist Dialog
                        if (uiState.showCreatePlaylistDialog) {
                            CreatePlaylistDialog(
                                isDarkTheme = uiState.isDarkTheme,
                                onDismiss = { viewModel.closeCreatePlaylistDialog() },
                                onCreate = { name, desc -> viewModel.createPlaylist(name, desc) }
                            )
                        }

                        // Add Track To Playlist Dialog
                        uiState.showAddToPlaylistTrack?.let { track ->
                            AddToPlaylistDialog(
                                track = track,
                                playlists = uiState.playlists,
                                isDarkTheme = uiState.isDarkTheme,
                                onDismiss = { viewModel.closeAddToPlaylistSheet() },
                                onSelectPlaylist = { plId -> viewModel.addTrackToPlaylist(plId, track.id) },
                                onCreateNewPlaylist = { viewModel.openCreatePlaylistDialog() }
                            )
                        }

                        // YouTube Track Import Dialog
                        if (uiState.showYouTubeImportDialog) {
                            YouTubeImportDialog(
                                isDarkTheme = uiState.isDarkTheme,
                                onDismiss = { viewModel.closeYouTubeImportDialog() },
                                onImport = { url, title, artist, genre ->
                                    viewModel.importTrackFromYouTube(url, title, artist, genre)
                                }
                            )
                        }



                        // Permanent App Link & Share Dialog
                        if (showShareAppDialog) {
                            ShareAppDialog(
                                isDarkTheme = uiState.isDarkTheme,
                                onDismiss = { showShareAppDialog = false }
                            )
                        }
                    }
                }
            }
        }
    }
}
