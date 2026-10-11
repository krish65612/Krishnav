package com.example.data

import com.example.model.*
import com.example.R

object TuneZxSampleData {

    val sampleSubscriptionTiers = listOf(
        SubscriptionTier(
            id = "tier_free",
            name = "Free Listener",
            priceMonthly = 0.0,
            priceDisplay = "₹0",
            description = "Standard streaming with ads every 4 songs",
            badgeName = "Listener",
            perks = listOf(
                "Standard audio quality",
                "Community comments",
                "Public playlist browsing"
            ),
            isPopular = false
        ),
        SubscriptionTier(
            id = "tier_backstage",
            name = "Vibelio VIP Premium",
            priceMonthly = 65.0,
            priceDisplay = "₹65/mo",
            description = "100% Ads Free, Downloads, High Quality & 3 Devices Connect",
            badgeName = "VIP Member",
            perks = listOf(
                "100% Ads Free (No ads every 4 songs)",
                "Download Songs for Offline Listening Anywhere",
                "Share Music & Uncompressed Tracks",
                "Listen in Ultra High Quality (24-bit Hi-Res)",
                "Connect up to 3 Devices simultaneously",
                "Exclusive VIP Fan Badge in comments",
                "Direct fan-to-artist Q&A chat access"
            ),
            isPopular = true
        ),
        SubscriptionTier(
            id = "tier_producer",
            name = "Inner Circle Producer",
            priceMonthly = 199.0,
            priceDisplay = "₹199/mo",
            description = "Multitrack stems, 1-on-1 feedback & tour pre-sale",
            badgeName = "Inner Circle",
            perks = listOf(
                "All ₹65 VIP perks included",
                "Downloadable stems & sample packs",
                "Direct 1-on-1 chat with artist",
                "Tour pre-sale ticket code priority",
                "Producer credit on monthly community EP"
            ),
            isPopular = false
        )
    )

    val sampleTourDates = listOf(
        TourDate("tour_1", "Brooklyn, NY", "Elsewhere Hall", "NOV 14, 2026", "VIP Available", true),
        TourDate("tour_2", "Berlin, Germany", "Astra Kulturhaus", "NOV 22, 2026", "Selling Fast", true),
        TourDate("tour_3", "Tokyo, Japan", "WWW X Shibuya", "DEC 05, 2026", "Sold Out", false),
        TourDate("tour_4", "London, UK", "Electric Brixton", "DEC 18, 2026", "VIP Available", true)
    )

    val artists = listOf(
        Artist(
            id = "artist_luna",
            name = "Luna Echoes",
            handle = "@lunaechoes",
            bio = "Independent synth-pop producer & vocalist creating ethereal night-drive anthems from Tokyo.",
            avatarRes = R.drawable.ic_tunezx_logo,
            bannerRes = R.drawable.img_artist_banner,
            followersCount = 142800L,
            monthlyListeners = 684200L,
            isFollowed = true,
            genres = listOf("Synthwave", "Dream Pop", "Electronic"),
            topCities = listOf("Tokyo", "Los Angeles", "Berlin", "London"),
            subscriptionTiers = sampleSubscriptionTiers,
            tourDates = sampleTourDates,
            isVerifiedIndie = true
        ),
        Artist(
            id = "artist_kairos",
            name = "Kairos Drift",
            handle = "@kairosdrift",
            bio = "Analog synth explorer, retrofuturistic beats & lo-fi chillscapes. 100% independent.",
            avatarRes = R.drawable.img_album_neon,
            bannerRes = R.drawable.img_artist_banner,
            followersCount = 98400L,
            monthlyListeners = 412000L,
            isFollowed = true,
            genres = listOf("Retrowave", "Cyberpunk", "Lo-Fi Beats"),
            topCities = listOf("Chicago", "Seoul", "Toronto", "Paris"),
            subscriptionTiers = sampleSubscriptionTiers,
            tourDates = sampleTourDates.take(2),
            isVerifiedIndie = true
        ),
        Artist(
            id = "artist_solara",
            name = "Solara Wave",
            handle = "@solarawave",
            bio = "Neo-soul guitars blended with atmospheric chillstep and deep groove basslines.",
            avatarRes = R.drawable.ic_tunezx_logo,
            bannerRes = R.drawable.img_artist_banner,
            followersCount = 67200L,
            monthlyListeners = 289000L,
            isFollowed = false,
            genres = listOf("Neo-Soul", "Indie Rock", "Ambient"),
            topCities = listOf("Austin", "Melbourne", "Amsterdam"),
            subscriptionTiers = sampleSubscriptionTiers,
            tourDates = sampleTourDates.drop(1),
            isVerifiedIndie = true
        )
    )

    val tracks = listOf(
        Track(
            id = "track_neon_horizons",
            title = "Neon Horizons",
            artistId = "artist_luna",
            artistName = "Luna Echoes",
            albumName = "Midnight Velocity",
            coverRes = R.drawable.img_album_neon,
            genre = "Synthwave",
            durationSeconds = 214,
            streamCount = 1845200L,
            likesCount = 94300,
            isLiked = true,
            releaseDate = "Oct 2026",
            isExclusive = false,
            hasAcousticVersion = true,
            hasStems = true,
            lyrics = "[Verse 1]\nCity lights reflect across the highway glass\nChasing shadows of a future moving fast\nEvery pulse of bass echoes in the haze\nLost inside an electric purple daze\n\n[Chorus]\nTake me through the neon horizons\nWhere the synth cuts like a diamond\nNo turning back, we ride the soundwave free\nJust you and the midnight velocity",
            syncedLyrics = listOf(
                SyncedLyricLine(0, "♪ (Intro - Analog Juno-106 Synth Arpeggio) ♪"),
                SyncedLyricLine(6, "City lights reflect across the highway glass"),
                SyncedLyricLine(14, "Chasing shadows of a future moving fast"),
                SyncedLyricLine(22, "Every pulse of bass echoes in the haze"),
                SyncedLyricLine(30, "Lost inside an electric purple daze"),
                SyncedLyricLine(40, "♪ (Synth Filter Sweep & Drum Build) ♪"),
                SyncedLyricLine(46, "Take me through the neon horizons"),
                SyncedLyricLine(54, "Where the synth cuts like a diamond"),
                SyncedLyricLine(64, "No turning back, we ride the soundwave free"),
                SyncedLyricLine(74, "Just you and the midnight velocity"),
                SyncedLyricLine(86, "Reflections neon across the dashboard glow"),
                SyncedLyricLine(96, "No speed limits where the night drivers go"),
                SyncedLyricLine(108, "Take me through the neon horizons!"),
                SyncedLyricLine(120, "♪ (Analog Lead Guitar Solo) ♪"),
                SyncedLyricLine(145, "Where the synth cuts like a diamond"),
                SyncedLyricLine(160, "Just you and the midnight velocity"),
                SyncedLyricLine(180, "♪ (Ambient Synth Reverb Outro) ♪")
            ),
            artistNotes = "Recorded using a vintage Juno-106 analog synthesizer and vocal harmonizer. Backstage Pass holders can hear the unreleased demo stem tracks!"
        ),
        Track(
            id = "track_cyber_pulse",
            title = "Cyber Pulse (VIP Mix)",
            artistId = "artist_kairos",
            artistName = "Kairos Drift",
            albumName = "Grid Runner 2099",
            coverRes = R.drawable.ic_tunezx_logo,
            genre = "Cyberpunk",
            durationSeconds = 188,
            streamCount = 942100L,
            likesCount = 61200,
            isLiked = false,
            releaseDate = "Sep 2026",
            isExclusive = true,
            minRequiredTier = "tier_backstage",
            hasAcousticVersion = false,
            hasStems = true,
            lyrics = "[Instrumental Electronic Drops]\n[Synthesizer Arpeggios at 128 BPM]\n[Resonant Low Pass Filter Sweep]\nHigh energy adrenaline soundscape.",
            syncedLyrics = listOf(
                SyncedLyricLine(0, "♪ (Cyber Sub-bass Pulse Intro) ♪"),
                SyncedLyricLine(12, "System online... Neural connection verified"),
                SyncedLyricLine(24, "Grid runner moving past the skyline neon"),
                SyncedLyricLine(36, "Binary frequencies beating in the night"),
                SyncedLyricLine(48, "♪ (Cyber Drop at 128 BPM) ♪"),
                SyncedLyricLine(70, "Pulse acceleration... Overclocking synths"),
                SyncedLyricLine(92, "Reaching maximum bandwidth through the matrix"),
                SyncedLyricLine(115, "♪ (Heavy Bassline Resonance) ♪"),
                SyncedLyricLine(142, "Indie soundwaves overriding the grid"),
                SyncedLyricLine(165, "♪ (Glitch Matrix Outro) ♪")
            ),
            artistNotes = "VIP Exclusive release! Multitrack stems available to Inner Circle members."
        ),
        Track(
            id = "track_velvet_twilight",
            title = "Velvet Twilight",
            artistId = "artist_solara",
            artistName = "Solara Wave",
            albumName = "Golden Hour Echoes",
            coverRes = R.drawable.img_artist_banner,
            genre = "Neo-Soul",
            durationSeconds = 236,
            streamCount = 621400L,
            likesCount = 38900,
            isLiked = true,
            releaseDate = "Aug 2026",
            isExclusive = false,
            hasAcousticVersion = true,
            hasStems = false,
            lyrics = "[Verse 1]\nWarm wind humming on the seaside terrace\nGuitar chords falling in gentle patterns\nNothing to hurry, nothing left to prove\nWrapped in velvet twilight groove",
            syncedLyrics = listOf(
                SyncedLyricLine(0, "♪ (Warm Acoustic Rhodes & Guitar Chords) ♪"),
                SyncedLyricLine(10, "Warm wind humming on the seaside terrace"),
                SyncedLyricLine(22, "Guitar chords falling in gentle patterns"),
                SyncedLyricLine(34, "Nothing to hurry, nothing left to prove"),
                SyncedLyricLine(46, "Wrapped in velvet twilight groove"),
                SyncedLyricLine(60, "Watching constellations slowly ignite"),
                SyncedLyricLine(74, "Whispering sweet melodies into the night"),
                SyncedLyricLine(88, "Nothing to hurry, nothing left to prove"),
                SyncedLyricLine(102, "Wrapped in velvet twilight groove"),
                SyncedLyricLine(124, "♪ (Soulful Saxophone & Lead Lick) ♪")
            ),
            artistNotes = "Written on an acoustic Martin guitar at 3 AM while watching the stars."
        ),
        Track(
            id = "track_tokyo_drift_rain",
            title = "Tokyo Rain Tape",
            artistId = "artist_kairos",
            artistName = "Kairos Drift",
            albumName = "Lo-Fi Shinjuku",
            coverRes = R.drawable.img_album_neon,
            genre = "Lo-Fi Beats",
            durationSeconds = 165,
            streamCount = 1320000L,
            likesCount = 82400,
            isLiked = false,
            releaseDate = "Oct 2026",
            isExclusive = false,
            hasAcousticVersion = false,
            hasStems = false,
            lyrics = "[Lo-fi vinyl crackle & warm Rhodes piano loop]",
            syncedLyrics = listOf(
                SyncedLyricLine(0, "♪ (Vinyl Crackle & Tokyo Rainfall) ♪"),
                SyncedLyricLine(15, "♪ (Warm Electric Piano Chords) ♪"),
                SyncedLyricLine(45, "♪ (Downtempo Lo-Fi Drum Beat Drops) ♪"),
                SyncedLyricLine(85, "♪ (Shinjuku Crossing Train Bell Chime) ♪"),
                SyncedLyricLine(120, "♪ (Mellow Tape Saturation Outro) ♪")
            ),
            artistNotes = "Field recording of rainfall in Shibuya blended with cassette saturation."
        ),
        Track(
            id = "track_starlight_echo",
            title = "Starlight Echo (Acoustic Unplugged)",
            artistId = "artist_luna",
            artistName = "Luna Echoes",
            albumName = "Raw Sessions Vol. 1",
            coverRes = R.drawable.img_artist_banner,
            genre = "Dream Pop",
            durationSeconds = 205,
            streamCount = 450100L,
            likesCount = 41200,
            isLiked = false,
            releaseDate = "Oct 2026",
            isExclusive = true,
            minRequiredTier = "tier_backstage",
            hasAcousticVersion = true,
            hasStems = true,
            lyrics = "[Verse 1]\nUnplugged, no synths tonight\nJust strings under candle light\nWhispering secrets to the night sky\nWatching all the starlight echo by",
            syncedLyrics = listOf(
                SyncedLyricLine(0, "♪ (Intimate Fingerpicked Guitar Intro) ♪"),
                SyncedLyricLine(8, "Unplugged, no synths tonight"),
                SyncedLyricLine(18, "Just strings under candle light"),
                SyncedLyricLine(28, "Whispering secrets to the night sky"),
                SyncedLyricLine(38, "Watching all the starlight echo by"),
                SyncedLyricLine(50, "Every breath a vibration in the quiet"),
                SyncedLyricLine(64, "Raw acoustics healing every riot"),
                SyncedLyricLine(78, "Watching all the starlight echo by"),
                SyncedLyricLine(92, "♪ (Acoustic Harmonizer Outro) ♪")
            ),
            artistNotes = "Intimate one-take studio vocal performance. Exclusive for Backstage Pass holders."
        )
    )

    val sampleDemographics = Demographics(
        topCountries = listOf(
            "United States" to 38,
            "Germany" to 19,
            "Japan" to 16,
            "United Kingdom" to 14,
            "Brazil" to 8,
            "Other" to 5
        ),
        ageDistribution = listOf(
            "18-24" to 44,
            "25-34" to 36,
            "35-44" to 13,
            "45+" to 7
        ),
        genderSplit = mapOf(
            "Female" to 49,
            "Male" to 45,
            "Non-binary / Other" to 6
        ),
        avgCompletionRate = 84,
        playlistSaves = 3840,
        hourlyStreams = listOf(
            "00:00" to 1240,
            "04:00" to 890,
            "08:00" to 3120,
            "12:00" to 4890,
            "16:00" to 6340,
            "20:00" to 8420
        ),
        totalRevenueEarned = 7842.50,
        pendingPayoutBalance = 1420.80
    )

    val initialPlaylists = listOf(
        Playlist(
            id = "pl_1",
            name = "Late Night Synth",
            description = "High octane indie synthwave for neon drives and night coding.",
            trackIds = listOf("track_neon_horizons", "track_cyber_pulse", "track_tokyo_drift_rain"),
            createdBy = "You",
            isPublic = true,
            shareCode = "vibelio.app/pl/synth-99",
            gradientColorIndex = 0
        ),
        Playlist(
            id = "pl_2",
            name = "Indie Gems & Acoustic",
            description = "Raw vocals, unplugged sessions and soul vibes.",
            trackIds = listOf("track_velvet_twilight", "track_starlight_echo"),
            createdBy = "You",
            isPublic = true,
            shareCode = "vibelio.app/pl/acoustic-gem",
            gradientColorIndex = 1
        )
    )

    val initialComments = listOf(
        Comment(
            id = "c_1",
            targetType = "SONG",
            targetId = "track_neon_horizons",
            authorName = "AriaVibes",
            authorBadge = "VIP Fan",
            text = "The bass drop at 1:45 literally gave me chills! Best indie release of the year.",
            timestamp = "2h ago",
            likes = 34,
            isLiked = true,
            isPinned = true,
            artistReply = "Thank you Aria! We tuned that analog filter for three whole days haha — Luna"
        ),
        Comment(
            id = "c_2",
            targetType = "SONG",
            targetId = "track_neon_horizons",
            authorName = "AlexSynthesizer",
            authorBadge = "Top 1% Fan",
            text = "Is that a real Juno-106 on the chorus chords? The chorus sounds huge!",
            timestamp = "5h ago",
            likes = 19,
            isLiked = false,
            isPinned = false,
            artistReply = "Yes! Directly routed through tape delay pedals! — Luna"
        ),
        Comment(
            id = "c_3",
            targetType = "ARTIST",
            targetId = "artist_luna",
            authorName = "RetroRunner",
            authorBadge = "Backstage Pass",
            text = "Can't wait for the Brooklyn Elsewhere concert next month! Got VIP tickets!",
            timestamp = "1d ago",
            likes = 48,
            isLiked = true,
            isPinned = true,
            artistReply = null
        ),
        Comment(
            id = "c_4",
            targetType = "SONG",
            targetId = "track_cyber_pulse",
            authorName = "CyberBeats99",
            authorBadge = "Inner Circle",
            text = "These stems are clean! Already remixing the percussion section.",
            timestamp = "3h ago",
            likes = 12,
            isLiked = false,
            isPinned = false
        )
    )

    val initialConversations = listOf(
        Conversation(
            id = "conv_luna",
            artistId = "artist_luna",
            artistName = "Luna Echoes",
            artistHandle = "@lunaechoes",
            avatarRes = R.drawable.ic_tunezx_logo,
            isVerified = true,
            isBroadcastChannel = false,
            unreadCount = 1,
            lastMessage = "Hey! Thanks so much for subscribing to the Backstage Pass! New acoustic demo drops Friday!",
            lastMessageTime = "12:45 PM",
            messages = listOf(
                Message("m1", "You", true, "Hey Luna! Loving the new track Neon Horizons!"),
                Message("m2", "Luna Echoes", false, "Hey! Thank you so much for the love! Which part is your favorite?"),
                Message("m3", "You", true, "The synth transition into the chorus is incredible."),
                Message("m4", "Luna Echoes", false, "Hey! Thanks so much for subscribing to the Backstage Pass! New acoustic demo drops Friday!")
            )
        ),
        Conversation(
            id = "conv_broadcast",
            artistId = "artist_broadcast",
            artistName = "Vibelio Indie Broadcast",
            artistHandle = "@vibelio_announcements",
            avatarRes = R.drawable.ic_vibelio_logo,
            isVerified = true,
            isBroadcastChannel = true,
            unreadCount = 0,
            lastMessage = "⚡ TOUR ALERT: Luna Echoes announces 4 international live dates! Early presale is now live for VIP subscribers.",
            lastMessageTime = "Yesterday",
            messages = listOf(
                Message(
                    id = "b1",
                    senderName = "Vibelio Editorial",
                    isFromUser = false,
                    text = "Welcome to the Official Indie Broadcast channel! You'll receive real-time updates when artists you follow drop new music or announce tour dates.",
                    timestamp = "Oct 8",
                    isBroadcastAnnouncement = true
                ),
                Message(
                    id = "b2",
                    senderName = "Vibelio Editorial",
                    isFromUser = false,
                    text = "⚡ TOUR ALERT: Luna Echoes announces 4 international live dates! Early presale is now live for VIP subscribers.",
                    timestamp = "Oct 9",
                    isBroadcastAnnouncement = true
                )
            )
        ),
        Conversation(
            id = "conv_kairos",
            artistId = "artist_kairos",
            artistName = "Kairos Drift",
            artistHandle = "@kairosdrift",
            avatarRes = R.drawable.img_album_neon,
            isVerified = true,
            isBroadcastChannel = false,
            unreadCount = 0,
            lastMessage = "Stems for Cyber Pulse just uploaded! Check your stems tab in the player.",
            lastMessageTime = "Oct 7",
            messages = listOf(
                Message("k1", "Kairos Drift", false, "Stems for Cyber Pulse just uploaded! Check your stems tab in the player.")
            )
        )
    )

    val initialCommunityPosts = listOf(
        CommunityPost(
            id = "post_1",
            artistId = "artist_luna",
            artistName = "Luna Echoes",
            artistHandle = "@lunaechoes",
            avatarRes = R.drawable.ic_vibelio_logo,
            content = "Excited to reveal 'Neon Horizons' is currently trending #1 on Vibelio Indie Chart! 🚀 Over 1.8M streams from all of you around the world. Tour rehearsals for Brooklyn and Berlin start this week!",
            attachedTrackId = "track_neon_horizons",
            timestamp = "4 hours ago",
            likes = 1420,
            isLiked = true,
            commentsCount = 89,
            repostsCount = 42
        ),
        CommunityPost(
            id = "post_2",
            artistId = "artist_kairos",
            artistName = "Kairos Drift",
            artistHandle = "@kairosdrift",
            avatarRes = R.drawable.img_album_neon,
            content = "For all my producer subscribers in the Inner Circle: the multi-track stems for 'Cyber Pulse' are now ready to download in the player sheet. Can't wait to hear your bootlegs and remixes! 🎛️",
            attachedTrackId = "track_cyber_pulse",
            timestamp = "1 day ago",
            likes = 890,
            isLiked = false,
            commentsCount = 37,
            repostsCount = 18
        )
    )

    val initialNotifications = listOf(
        AppNotification(
            id = "notif_1",
            title = "🔥 New Release Out Now!",
            body = "Luna Echoes just released 'Neon Horizons'. Tap to listen now.",
            type = NotificationType.NEW_RELEASE,
            timestamp = "Just now",
            relatedId = "track_neon_horizons",
            isRead = false
        ),
        AppNotification(
            id = "notif_2",
            title = "🎫 Tour Presale Announced!",
            body = "Kairos Drift and Luna Echoes added dates in NYC & Berlin. VIP presale live.",
            type = NotificationType.TOUR_ALERT,
            timestamp = "2 hours ago",
            relatedId = "artist_luna",
            isRead = false
        ),
        AppNotification(
            id = "notif_3",
            title = "👑 VIP Exclusive Drop",
            body = "Unreleased Acoustic Session of 'Starlight Echo' is now streaming.",
            type = NotificationType.EXCLUSIVE_DROP,
            timestamp = "1 day ago",
            relatedId = "track_starlight_echo",
            isRead = true
        )
    )
}
