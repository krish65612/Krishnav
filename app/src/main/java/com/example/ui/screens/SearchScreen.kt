package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Track
import com.example.ui.theme.*

data class GenreCategory(
    val title: String,
    val color: Color,
    val coverRes: Int
)

data class TrendingTag(
    val hashtag: String,
    val coverRes: Int
)

@Composable
fun SearchScreen(
    searchQuery: String,
    tracks: List<Track>,
    isDarkTheme: Boolean,
    onQueryChange: (String) -> Unit,
    onTrackClick: (Track) -> Unit
) {
    val bgColor = if (isDarkTheme) ObsidianBlack else PureWhite
    val textColor = if (isDarkTheme) TextWhitePrimary else TextDarkPrimary
    val textSub = if (isDarkTheme) TextWhiteSecondary else TextDarkSecondary

    val searchInputBg = if (isDarkTheme) PureWhite else Color(0xFFF1F3F5)
    val searchInputText = Color.Black

    val trendingTags = listOf(
        TrendingTag("#indie pop", com.example.R.drawable.img_artist_banner),
        TrendingTag("#lofi beats", com.example.R.drawable.img_album_neon),
        TrendingTag("#synthwave", com.example.R.drawable.ic_tunezx_logo),
        TrendingTag("#acoustic", com.example.R.drawable.img_artist_banner)
    )

    val genreCategories = listOf(
        GenreCategory("Upcoming releases", Color(0xFF00796B), com.example.R.drawable.ic_tunezx_logo),
        GenreCategory("New Releases", Color(0xFF558B2F), com.example.R.drawable.img_album_neon),
        GenreCategory("Rain & Chill", Color(0xFF1976D2), com.example.R.drawable.img_artist_banner),
        GenreCategory("Indie Pop", Color(0xFFC2185B), com.example.R.drawable.img_album_neon),
        GenreCategory("Alt Rock", Color(0xFFE65100), com.example.R.drawable.img_artist_banner),
        GenreCategory("Lo-Fi & Beats", Color(0xFF7B1FA2), com.example.R.drawable.ic_tunezx_logo),
        GenreCategory("Indie Charts", Color(0xFF512DA8), com.example.R.drawable.img_album_neon),
        GenreCategory("VIP Stems", Color(0xFF0D47A1), com.example.R.drawable.img_artist_banner)
    )

    val filteredTracks = remember(searchQuery, tracks) {
        if (searchQuery.isBlank()) emptyList()
        else tracks.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
            it.artistName.contains(searchQuery, ignoreCase = true) ||
            it.genre.contains(searchQuery, ignoreCase = true)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .padding(horizontal = 16.dp)
            .testTag("search_screen")
    ) {
        // Search Input Bar matching user's screenshot
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(searchInputBg)
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onQueryChange,
                        placeholder = {
                            Text(
                                text = "What do you want to listen to?",
                                color = Color(0xFF555555),
                                fontSize = 14.sp
                            )
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = searchInputText,
                            unfocusedTextColor = searchInputText,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("search_text_input")
                    )
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onQueryChange("") }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.Black)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        if (searchQuery.isNotBlank()) {
            // Live Search Results
            item {
                Text(
                    text = "Songs & Artists for \"$searchQuery\"",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (filteredTracks.isEmpty()) {
                item {
                    Text(
                        text = "No tracks found matching \"$searchQuery\". Try exploring trending categories below.",
                        color = textSub,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(vertical = 20.dp)
                    )
                }
            } else {
                items(filteredTracks) { track ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
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
                            Text(
                                text = "${track.artistName} • ${track.genre}",
                                color = textSub,
                                fontSize = 12.sp
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.PlayCircle,
                            contentDescription = "Play",
                            tint = NeonCyan,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        } else {
            // Section 1: Vertical Short Video / Story Tags Carousel (matching screenshot 2)
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(end = 16.dp)
                ) {
                    items(trendingTags) { tag ->
                        Box(
                            modifier = Modifier
                                .width(120.dp)
                                .height(210.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onQueryChange(tag.hashtag.removePrefix("#")) }
                        ) {
                            Image(
                                painter = painterResource(id = tag.coverRes),
                                contentDescription = tag.hashtag,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = tag.hashtag,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Section 2: 2-Column Vibrant Genre Category Tiles (matching screenshot 2)
            item {
                Text(
                    text = "Browse all",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Render 2-column tiles in rows
            val rows = genreCategories.chunked(2)
            items(rows) { rowItems ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowItems.forEach { cat ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(95.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(cat.color)
                                .clickable { onQueryChange(cat.title) }
                        ) {
                            Text(
                                text = cat.title,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .padding(12.dp)
                                    .align(Alignment.TopStart)
                            )

                            // Angled decorative thumbnail in bottom right corner (classic Spotify tile aesthetic)
                            Image(
                                painter = painterResource(id = cat.coverRes),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(60.dp)
                                    .align(Alignment.BottomEnd)
                                    .offset(x = 12.dp, y = 12.dp)
                                    .rotate(24f)
                                    .clip(RoundedCornerShape(4.dp))
                            )
                        }
                    }
                    if (rowItems.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}
