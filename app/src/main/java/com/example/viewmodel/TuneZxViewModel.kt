package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.TuneZxSampleData
import com.example.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

enum class AppTab {
    HOME,
    SEARCH,
    LIBRARY,
    ANALYTICS,
    COMMUNITY,
    MESSAGES
}

data class UiState(
    val isDarkTheme: Boolean = true, // White & Black theme toggle
    val currentTab: AppTab = AppTab.HOME,
    val selectedHomeChip: String = "All",
    val searchQuery: String = "",
    val activeTrackDetailId: String? = null,
    val activeArtistDetailId: String? = null,
    val activePlaylistDetailId: String? = null,
    val activeConversationId: String? = null,
    val isFullPlayerVisible: Boolean = false,
    val showPaymentModalForTier: SubscriptionTier? = null,
    val showCreatePlaylistDialog: Boolean = false,
    val showAddToPlaylistTrack: Track? = null,
    val showArtistModerationPanel: Boolean = false,
    val activePushBanner: AppNotification? = null,

    // Audio Playback State
    val currentTrack: Track? = TuneZxSampleData.tracks.first(),
    val isPlaying: Boolean = false,
    val playbackPositionSec: Int = 24,
    val isShuffle: Boolean = false,
    val isRepeat: Boolean = false,

    // Core Data
    val tracks: List<Track> = TuneZxSampleData.tracks,
    val artists: List<Artist> = TuneZxSampleData.artists,
    val playlists: List<Playlist> = TuneZxSampleData.initialPlaylists,
    val comments: List<Comment> = TuneZxSampleData.initialComments,
    val conversations: List<Conversation> = TuneZxSampleData.initialConversations,
    val communityPosts: List<CommunityPost> = TuneZxSampleData.initialCommunityPosts,
    val notifications: List<AppNotification> = TuneZxSampleData.initialNotifications,
    val demographics: Demographics = TuneZxSampleData.sampleDemographics,

    // User Membership & Royalties
    val userSubscribedTierId: String = "tier_free",
    val profanityFilterEnabled: Boolean = true,
    val analyticsTimeRange: String = "Last 7 Days",
    val userSavedTrackIds: Set<String> = setOf("track_neon_horizons", "track_velvet_twilight"),
    val followedArtistIds: Set<String> = setOf("artist_luna", "artist_kairos"),
    val downloadedTrackIds: Set<String> = setOf("track_neon_horizons"),
    val isLyricsOverlayVisible: Boolean = false,
    val showYouTubeImportDialog: Boolean = false,
    val songsPlayedCount: Int = 0,
    val showAdOverlay: Boolean = false
)

class TuneZxViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var playbackJob: Job? = null

    init {
        startPlaybackTicker()
    }

    private fun startPlaybackTicker() {
        playbackJob?.cancel()
        playbackJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                if (_uiState.value.isPlaying) {
                    _uiState.update { state ->
                        val current = state.currentTrack ?: return@update state
                        val nextPos = state.playbackPositionSec + 1
                        if (nextPos >= current.durationSeconds) {
                            if (state.isRepeat) {
                                state.copy(playbackPositionSec = 0)
                            } else {
                                // auto advance to next
                                val nextTrack = getNextTrack(current, state.tracks, state.isShuffle)
                                state.copy(currentTrack = nextTrack, playbackPositionSec = 0)
                            }
                        } else {
                            state.copy(playbackPositionSec = nextPos)
                        }
                    }
                }
            }
        }
    }

    private fun getNextTrack(current: Track, tracks: List<Track>, shuffle: Boolean): Track {
        if (tracks.isEmpty()) return current
        if (shuffle) return tracks.random()
        val index = tracks.indexOfFirst { it.id == current.id }
        return if (index != -1 && index + 1 < tracks.size) tracks[index + 1] else tracks.first()
    }

    private fun getPrevTrack(current: Track, tracks: List<Track>): Track {
        if (tracks.isEmpty()) return current
        val index = tracks.indexOfFirst { it.id == current.id }
        return if (index > 0) tracks[index - 1] else tracks.last()
    }

    // Theme Switcher (White and Black)
    fun toggleTheme() {
        _uiState.update { it.copy(isDarkTheme = !it.isDarkTheme) }
    }

    fun setDarkTheme(dark: Boolean) {
        _uiState.update { it.copy(isDarkTheme = dark) }
    }

    // Navigation
    fun selectTab(tab: AppTab) {
        _uiState.update {
            it.copy(
                currentTab = tab,
                activeTrackDetailId = null,
                activeArtistDetailId = null,
                activePlaylistDetailId = null,
                activeConversationId = null
            )
        }
    }

    fun selectHomeChip(chip: String) {
        _uiState.update { it.copy(selectedHomeChip = chip) }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun openTrackDetail(trackId: String) {
        _uiState.update { it.copy(activeTrackDetailId = trackId) }
    }

    fun openArtistDetail(artistId: String) {
        _uiState.update { it.copy(activeArtistDetailId = artistId) }
    }

    fun openPlaylistDetail(playlistId: String) {
        _uiState.update { it.copy(activePlaylistDetailId = playlistId) }
    }

    fun openConversation(conversationId: String) {
        _uiState.update { state ->
            val updatedConversations = state.conversations.map {
                if (it.id == conversationId) it.copy(unreadCount = 0) else it
            }
            state.copy(
                activeConversationId = conversationId,
                conversations = updatedConversations
            )
        }
    }

    fun closeSubScreens() {
        _uiState.update {
            it.copy(
                activeTrackDetailId = null,
                activeArtistDetailId = null,
                activePlaylistDetailId = null,
                activeConversationId = null
            )
        }
    }

    // Audio Playback Controls
    fun playTrack(track: Track) {
        val currentCount = _uiState.value.songsPlayedCount + 1
        val shouldShowAd = currentCount % 4 == 0 && _uiState.value.userSubscribedTierId == "tier_free"

        _uiState.update {
            it.copy(
                currentTrack = track,
                isPlaying = !shouldShowAd,
                playbackPositionSec = 0,
                songsPlayedCount = currentCount,
                showAdOverlay = shouldShowAd
            )
        }
    }

    fun dismissAd() {
        _uiState.update { it.copy(showAdOverlay = false, isPlaying = true) }
    }

    fun togglePlayPause() {
        _uiState.update { it.copy(isPlaying = !it.isPlaying) }
    }

    fun seekTo(seconds: Int) {
        _uiState.update { it.copy(playbackPositionSec = seconds) }
    }

    fun playNext() {
        _uiState.update { state ->
            val cur = state.currentTrack ?: return@update state
            val next = getNextTrack(cur, state.tracks, state.isShuffle)
            state.copy(currentTrack = next, playbackPositionSec = 0, isPlaying = true)
        }
    }

    fun playPrev() {
        _uiState.update { state ->
            val cur = state.currentTrack ?: return@update state
            val prev = getPrevTrack(cur, state.tracks)
            state.copy(currentTrack = prev, playbackPositionSec = 0, isPlaying = true)
        }
    }

    fun toggleShuffle() {
        _uiState.update { it.copy(isShuffle = !it.isShuffle) }
    }

    fun toggleRepeat() {
        _uiState.update { it.copy(isRepeat = !it.isRepeat) }
    }

    fun setFullPlayerVisible(visible: Boolean) {
        _uiState.update { it.copy(isFullPlayerVisible = visible) }
    }

    // Favorites & Following
    fun toggleLikeTrack(trackId: String) {
        _uiState.update { state ->
            val isCurrentlySaved = state.userSavedTrackIds.contains(trackId)
            val updatedSaved = if (isCurrentlySaved) {
                state.userSavedTrackIds - trackId
            } else {
                state.userSavedTrackIds + trackId
            }
            val updatedTracks = state.tracks.map { track ->
                if (track.id == trackId) {
                    val willBeLiked = !track.isLiked
                    track.copy(
                        isLiked = willBeLiked,
                        likesCount = if (willBeLiked) track.likesCount + 1 else track.likesCount - 1
                    )
                } else track
            }
            state.copy(
                userSavedTrackIds = updatedSaved,
                tracks = updatedTracks,
                currentTrack = if (state.currentTrack?.id == trackId) {
                    state.currentTrack.copy(isLiked = !state.currentTrack.isLiked)
                } else state.currentTrack
            )
        }
    }

    fun setLyricsOverlayVisible(visible: Boolean) {
        _uiState.update { it.copy(isLyricsOverlayVisible = visible) }
    }

    fun toggleDownloadTrack(trackId: String) {
        val state = _uiState.value
        // If free tier, prompt to get ₹65 subscription to download
        if (state.userSubscribedTierId == "tier_free") {
            val tier = TuneZxSampleData.sampleSubscriptionTiers.find { it.id == "tier_backstage" }
            if (tier != null) promptSubscribe(tier)
            return
        }

        // Subscribed: toggle download status
        val isDownloaded = state.downloadedTrackIds.contains(trackId)
        val updatedDownloaded = if (isDownloaded) state.downloadedTrackIds - trackId else state.downloadedTrackIds + trackId
        val track = state.tracks.find { it.id == trackId }

        val downloadNotif = if (!isDownloaded && track != null) {
            AppNotification(
                id = "notif_dl_${System.currentTimeMillis()}",
                title = "⬇️ Track Downloaded!",
                body = "'${track.title}' is now saved locally for offline listening.",
                type = NotificationType.FAN_SUBSCRIPTION,
                timestamp = "Just now",
                isRead = false
            )
        } else null

        _uiState.update { cur ->
            cur.copy(
                downloadedTrackIds = updatedDownloaded,
                notifications = if (downloadNotif != null) listOf(downloadNotif) + cur.notifications else cur.notifications,
                activePushBanner = downloadNotif ?: cur.activePushBanner
            )
        }
    }

    fun toggleFollowArtist(artistId: String) {
        _uiState.update { state ->
            val isFollowed = state.followedArtistIds.contains(artistId)
            val updated = if (isFollowed) state.followedArtistIds - artistId else state.followedArtistIds + artistId
            val updatedArtists = state.artists.map {
                if (it.id == artistId) it.copy(isFollowed = !isFollowed) else it
            }
            state.copy(followedArtistIds = updated, artists = updatedArtists)
        }
    }

    fun openYouTubeImportDialog() {
        _uiState.update { it.copy(showYouTubeImportDialog = true) }
    }

    fun closeYouTubeImportDialog() {
        _uiState.update { it.copy(showYouTubeImportDialog = false) }
    }

    fun importTrackFromYouTube(youtubeUrl: String, title: String, artistName: String, genre: String) {
        val cleanTitle = if (title.isNotBlank()) title.trim() else "YouTube Original (${youtubeUrl.takeLast(6)})"
        val cleanArtist = if (artistName.isNotBlank()) artistName.trim() else "Independent Creator"
        val cleanGenre = if (genre.isNotBlank()) genre.trim() else "Indie Pop"
        
        val newTrack = Track(
            id = "yt_${UUID.randomUUID().toString().take(6)}",
            title = cleanTitle,
            artistId = "artist_imported",
            artistName = cleanArtist,
            albumName = "YouTube Imports",
            coverRes = com.example.R.drawable.ic_tunezx_logo,
            genre = cleanGenre,
            durationSeconds = 195,
            streamCount = 1L,
            likesCount = 1,
            isLiked = true,
            releaseDate = "Imported Today",
            isExclusive = false,
            lyrics = "Imported audio stream from YouTube: $youtubeUrl",
            artistNotes = "Directly imported into your Vibelio cloud library from $youtubeUrl"
        )

        val importNotif = AppNotification(
            id = "notif_yt_${System.currentTimeMillis()}",
            title = "🎵 YouTube Track Imported!",
            body = "'$cleanTitle' by $cleanArtist is now available in your Vibelio library.",
            type = NotificationType.NEW_RELEASE,
            timestamp = "Just now",
            relatedId = newTrack.id,
            isRead = false
        )

        _uiState.update { state ->
            state.copy(
                tracks = listOf(newTrack) + state.tracks,
                userSavedTrackIds = state.userSavedTrackIds + newTrack.id,
                showYouTubeImportDialog = false,
                currentTrack = newTrack,
                isPlaying = true,
                playbackPositionSec = 0,
                notifications = listOf(importNotif) + state.notifications,
                activePushBanner = importNotif
            )
        }
    }

    // Playlists Feature
    fun openCreatePlaylistDialog() {
        _uiState.update { it.copy(showCreatePlaylistDialog = true) }
    }

    fun closeCreatePlaylistDialog() {
        _uiState.update { it.copy(showCreatePlaylistDialog = false) }
    }

    fun createPlaylist(name: String, description: String) {
        val newPl = Playlist(
            id = "pl_${UUID.randomUUID().toString().take(6)}",
            name = name.ifBlank { "My Indie Mix" },
            description = description.ifBlank { "Curated with Vibelio independent music." },
            trackIds = emptyList(),
            createdBy = "You",
            isPublic = true,
            shareCode = "vibelio.app/pl/${name.lowercase().replace(" ", "-")}-${(10..99).random()}",
            gradientColorIndex = (_uiState.value.playlists.size) % 3
        )
        _uiState.update {
            it.copy(
                playlists = it.playlists + newPl,
                showCreatePlaylistDialog = false
            )
        }
    }

    fun updatePlaylistName(playlistId: String, newName: String, newDescription: String) {
        if (newName.isBlank()) return
        _uiState.update { state ->
            val updated = state.playlists.map { pl ->
                if (pl.id == playlistId) pl.copy(name = newName.trim(), description = newDescription.trim()) else pl
            }
            state.copy(playlists = updated)
        }
    }

    fun openAddToPlaylistSheet(track: Track) {
        _uiState.update { it.copy(showAddToPlaylistTrack = track) }
    }

    fun closeAddToPlaylistSheet() {
        _uiState.update { it.copy(showAddToPlaylistTrack = null) }
    }

    fun addTrackToPlaylist(playlistId: String, trackId: String) {
        _uiState.update { state ->
            val updated = state.playlists.map { pl ->
                if (pl.id == playlistId && !pl.trackIds.contains(trackId)) {
                    pl.copy(trackIds = pl.trackIds + trackId)
                } else pl
            }
            state.copy(playlists = updated, showAddToPlaylistTrack = null)
        }
    }

    fun removeTrackFromPlaylist(playlistId: String, trackId: String) {
        _uiState.update { state ->
            val updated = state.playlists.map { pl ->
                if (pl.id == playlistId) pl.copy(trackIds = pl.trackIds - trackId) else pl
            }
            state.copy(playlists = updated)
        }
    }

    fun reorderPlaylistTrack(playlistId: String, fromIndex: Int, toIndex: Int) {
        _uiState.update { state ->
            val playlist = state.playlists.find { it.id == playlistId } ?: return@update state
            val list = playlist.trackIds.toMutableList()
            if (fromIndex in list.indices && toIndex in list.indices) {
                val item = list.removeAt(fromIndex)
                list.add(toIndex, item)
                val updated = state.playlists.map {
                    if (it.id == playlistId) it.copy(trackIds = list) else it
                }
                state.copy(playlists = updated)
            } else state
        }
    }

    // Commenting & Artist Moderation System
    fun addComment(targetType: String, targetId: String, text: String) {
        if (text.isBlank()) return
        val userBadge = when (_uiState.value.userSubscribedTierId) {
            "tier_producer" -> "Inner Circle"
            "tier_backstage" -> "VIP Fan"
            else -> "Day 1 Fan"
        }
        val newComment = Comment(
            id = "c_${UUID.randomUUID().toString().take(6)}",
            targetType = targetType,
            targetId = targetId,
            authorName = "You",
            authorBadge = userBadge,
            text = text.trim(),
            timestamp = "Just now",
            likes = 0,
            isLiked = false,
            isPinned = false,
            isHidden = false
        )
        _uiState.update { it.copy(comments = listOf(newComment) + it.comments) }
    }

    fun toggleCommentLike(commentId: String) {
        _uiState.update { state ->
            val updated = state.comments.map { c ->
                if (c.id == commentId) {
                    val willLike = !c.isLiked
                    c.copy(isLiked = willLike, likes = if (willLike) c.likes + 1 else c.likes - 1)
                } else c
            }
            state.copy(comments = updated)
        }
    }

    // Artist Moderation Controls
    fun togglePinComment(commentId: String) {
        _uiState.update { state ->
            val updated = state.comments.map {
                if (it.id == commentId) it.copy(isPinned = !it.isPinned) else it
            }
            state.copy(comments = updated)
        }
    }

    fun toggleHideComment(commentId: String) {
        _uiState.update { state ->
            val updated = state.comments.map {
                if (it.id == commentId) it.copy(isHidden = !it.isHidden) else it
            }
            state.copy(comments = updated)
        }
    }

    fun deleteComment(commentId: String) {
        _uiState.update { state ->
            state.copy(comments = state.comments.filterNot { it.id == commentId })
        }
    }

    fun addArtistReply(commentId: String, replyText: String) {
        _uiState.update { state ->
            val updated = state.comments.map {
                if (it.id == commentId) it.copy(artistReply = replyText) else it
            }
            state.copy(comments = updated)
        }
    }

    fun toggleProfanityFilter() {
        _uiState.update { it.copy(profanityFilterEnabled = !it.profanityFilterEnabled) }
    }

    // Direct Messaging Portal & Broadcasts
    fun sendMessage(conversationId: String, text: String) {
        if (text.isBlank()) return
        val userMsg = Message(
            id = "msg_${System.currentTimeMillis()}",
            senderName = "You",
            isFromUser = true,
            text = text.trim(),
            timestamp = "Just now"
        )
        _uiState.update { state ->
            val updatedConvs = state.conversations.map { conv ->
                if (conv.id == conversationId) {
                    conv.copy(
                        lastMessage = text.trim(),
                        lastMessageTime = "Just now",
                        messages = conv.messages + userMsg
                    )
                } else conv
            }
            state.copy(conversations = updatedConvs)
        }

        // Simulate artist quick reply
        viewModelScope.launch {
            delay(1800)
            val currentConv = _uiState.value.conversations.find { it.id == conversationId }
            if (currentConv != null && !currentConv.isBroadcastChannel) {
                val replies = listOf(
                    "Love this! Thanks for streaming on Vibelio!",
                    "Appreciate the support so much. Working on the new EP right now!",
                    "That melody was inspired by Tokyo night drives. Glad it connected with you!",
                    "See you at the upcoming tour dates!"
                )
                val artistReplyMsg = Message(
                    id = "msg_${System.currentTimeMillis()}",
                    senderName = currentConv.artistName,
                    isFromUser = false,
                    text = replies.random(),
                    timestamp = "Just now"
                )
                _uiState.update { state ->
                    val updated = state.conversations.map { c ->
                        if (c.id == conversationId) {
                            c.copy(
                                lastMessage = artistReplyMsg.text,
                                lastMessageTime = "Just now",
                                messages = c.messages + artistReplyMsg
                            )
                        } else c
                    }
                    state.copy(conversations = updated)
                }
            }
        }
    }

    // Community Feed (Likes, Reposts)
    fun togglePostLike(postId: String) {
        _uiState.update { state ->
            val updated = state.communityPosts.map { post ->
                if (post.id == postId) {
                    val willLike = !post.isLiked
                    post.copy(
                        isLiked = willLike,
                        likes = if (willLike) post.likes + 1 else post.likes - 1
                    )
                } else post
            }
            state.copy(communityPosts = updated)
        }
    }

    // Subscriptions & Payment Gateway
    fun promptSubscribe(tier: SubscriptionTier) {
        _uiState.update { it.copy(showPaymentModalForTier = tier) }
    }

    fun dismissPaymentModal() {
        _uiState.update { it.copy(showPaymentModalForTier = null) }
    }

    fun completeSubscription(tierId: String) {
        _uiState.update { state ->
            val tier = TuneZxSampleData.sampleSubscriptionTiers.find { it.id == tierId }
            val welcomeNotif = AppNotification(
                id = "notif_sub_${System.currentTimeMillis()}",
                title = "🎉 VIP Badge Unlocked!",
                body = "Welcome to ${tier?.name ?: "VIP"}! All exclusive acoustic stems are now available.",
                type = NotificationType.FAN_SUBSCRIPTION,
                timestamp = "Just now",
                isRead = false
            )
            state.copy(
                userSubscribedTierId = tierId,
                showPaymentModalForTier = null,
                notifications = listOf(welcomeNotif) + state.notifications,
                activePushBanner = welcomeNotif
            )
        }
    }

    // Push Notifications & Alerts
    fun dismissPushBanner() {
        _uiState.update { it.copy(activePushBanner = null) }
    }

    fun triggerSimulatedReleaseAlert() {
        val newNotif = AppNotification(
            id = "notif_rel_${System.currentTimeMillis()}",
            title = "🚨 NEW DROP: 'Electric Eclipse'",
            body = "Luna Echoes & Kairos Drift just dropped their collaborative single! Tap to stream.",
            type = NotificationType.NEW_RELEASE,
            timestamp = "Just now",
            relatedId = "track_neon_horizons",
            isRead = false
        )
        _uiState.update {
            it.copy(
                notifications = listOf(newNotif) + it.notifications,
                activePushBanner = newNotif
            )
        }
    }

    fun triggerSimulatedTourAlert() {
        val newNotif = AppNotification(
            id = "notif_tour_${System.currentTimeMillis()}",
            title = "🎫 TOUR ANNOUNCEMENT: Europe & North America",
            body = "Tickets for Luna Echoes World Tour are now open for presale reservation.",
            type = NotificationType.TOUR_ALERT,
            timestamp = "Just now",
            relatedId = "artist_luna",
            isRead = false
        )
        _uiState.update {
            it.copy(
                notifications = listOf(newNotif) + it.notifications,
                activePushBanner = newNotif
            )
        }
    }

    fun markAllNotificationsRead() {
        _uiState.update { state ->
            val updated = state.notifications.map { it.copy(isRead = true) }
            state.copy(notifications = updated)
        }
    }

    // Real-Time Analytics & Instant Payout
    fun setAnalyticsTimeRange(range: String) {
        _uiState.update { it.copy(analyticsTimeRange = range) }
    }

    fun requestInstantPayout() {
        _uiState.update { state ->
            val curDemo = state.demographics
            val updatedDemo = curDemo.copy(
                totalRevenueEarned = curDemo.totalRevenueEarned + curDemo.pendingPayoutBalance,
                pendingPayoutBalance = 0.0
            )
            val payoutNotif = AppNotification(
                id = "notif_pay_${System.currentTimeMillis()}",
                title = "💰 Payout Dispatched Successfully",
                body = "Your royalties of $1,420.80 have been wired to your verified Indie Bank Account.",
                type = NotificationType.FAN_SUBSCRIPTION,
                timestamp = "Just now",
                isRead = false
            )
            state.copy(
                demographics = updatedDemo,
                notifications = listOf(payoutNotif) + state.notifications,
                activePushBanner = payoutNotif
            )
        }
    }
}
