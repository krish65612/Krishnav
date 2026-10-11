package com.example.model

data class Track(
    val id: String,
    val title: String,
    val artistId: String,
    val artistName: String,
    val albumName: String,
    val coverRes: Int,
    val genre: String,
    val durationSeconds: Int,
    val streamCount: Long,
    val likesCount: Int,
    val isLiked: Boolean = false,
    val releaseDate: String,
    val isExclusive: Boolean = false,
    val minRequiredTier: String? = null,
    val hasAcousticVersion: Boolean = false,
    val hasStems: Boolean = false,
    val lyrics: String = "",
    val syncedLyrics: List<SyncedLyricLine> = emptyList(),
    val artistNotes: String = "",
    val audioWaveform: List<Float> = listOf(0.3f, 0.6f, 0.9f, 0.4f, 0.7f, 0.5f, 0.8f, 0.3f, 0.6f, 1f, 0.7f, 0.4f, 0.9f, 0.5f, 0.3f, 0.8f, 0.6f, 0.4f, 0.7f, 0.5f)
)

data class SyncedLyricLine(
    val timestampSec: Int,
    val text: String
)

data class SubscriptionTier(
    val id: String,
    val name: String,
    val priceMonthly: Double,
    val priceDisplay: String = "₹65/mo",
    val description: String,
    val badgeName: String,
    val perks: List<String>,
    val isPopular: Boolean = false
)

data class TourDate(
    val id: String,
    val city: String,
    val venue: String,
    val date: String,
    val ticketStatus: String, // "Selling Fast", "VIP Available", "Sold Out"
    val isVipEligible: Boolean = true
)

data class Artist(
    val id: String,
    val name: String,
    val handle: String,
    val bio: String,
    val avatarRes: Int,
    val bannerRes: Int,
    val followersCount: Long,
    val monthlyListeners: Long,
    val isFollowed: Boolean = false,
    val genres: List<String>,
    val topCities: List<String>,
    val subscriptionTiers: List<SubscriptionTier>,
    val tourDates: List<TourDate> = emptyList(),
    val isVerifiedIndie: Boolean = true
)

data class Demographics(
    val topCountries: List<Pair<String, Int>>, // e.g. "USA" to 42%
    val ageDistribution: List<Pair<String, Int>>, // e.g. "18-24" to 45%
    val genderSplit: Map<String, Int>, // "Female" to 48, "Male" to 46, "Other" to 6
    val avgCompletionRate: Int, // 84%
    val playlistSaves: Int, // 1420
    val hourlyStreams: List<Pair<String, Int>>,
    val totalRevenueEarned: Double,
    val pendingPayoutBalance: Double
)

data class Playlist(
    val id: String,
    val name: String,
    val description: String,
    val trackIds: List<String>,
    val createdBy: String,
    val isPublic: Boolean = true,
    val shareCode: String,
    val gradientColorIndex: Int = 0
)

data class Comment(
    val id: String,
    val targetType: String, // "SONG" or "ARTIST"
    val targetId: String,
    val authorName: String,
    val authorBadge: String? = null, // "VIP Fan", "Superfan", "Artist Verified"
    val text: String,
    val timestamp: String,
    val likes: Int = 0,
    val isLiked: Boolean = false,
    val isPinned: Boolean = false,
    val isHidden: Boolean = false,
    val artistReply: String? = null
)

data class Message(
    val id: String,
    val senderName: String,
    val isFromUser: Boolean,
    val text: String,
    val timestamp: String = "Just now",
    val isBroadcastAnnouncement: Boolean = false
)

data class Conversation(
    val id: String,
    val artistId: String,
    val artistName: String,
    val artistHandle: String,
    val avatarRes: Int,
    val isVerified: Boolean = true,
    val isBroadcastChannel: Boolean = false,
    val unreadCount: Int = 0,
    val lastMessage: String,
    val lastMessageTime: String,
    val messages: List<Message>
)

data class CommunityPost(
    val id: String,
    val artistId: String,
    val artistName: String,
    val artistHandle: String,
    val avatarRes: Int,
    val content: String,
    val attachedTrackId: String? = null,
    val timestamp: String,
    val likes: Int,
    val isLiked: Boolean = false,
    val commentsCount: Int,
    val repostsCount: Int
)

data class AppNotification(
    val id: String,
    val title: String,
    val body: String,
    val type: NotificationType,
    val timestamp: String,
    val relatedId: String? = null, // trackId or artistId
    val isRead: Boolean = false
)

enum class NotificationType {
    NEW_RELEASE,
    TOUR_ALERT,
    EXCLUSIVE_DROP,
    DIRECT_MESSAGE,
    FAN_SUBSCRIPTION
}
