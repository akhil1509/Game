package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.CurrentScreen
import com.example.game.GameViewModel
import com.example.model.GAME_CHAPTERS

@Composable
fun LevelSelectScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val levelProgressList by viewModel.levelProgressList.collectAsState()
    val stats by viewModel.gameStats.collectAsState()
    val totalStars = stats?.totalStarsCollected ?: 0

    var selectedChapterIndex by remember { mutableIntStateOf(0) }
    val currentChapter = GAME_CHAPTERS[selectedChapterIndex]

    val chapterLevels = levelProgressList.filter { it.levelNumber in currentChapter.levelRange }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0x331E3A8A),
                        Color(0x1A0F172A),
                        Color(0xFF0F172A)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Immersive Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Back Button: w-11 h-11 bg-white/10 rounded-2xl border border-white/10
                Box(
                    modifier = Modifier
                        .testTag("back_button")
                        .size(44.dp)
                        .background(Color(0x1AFFFFFF), RoundedCornerShape(16.dp))
                        .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(16.dp))
                        .clickable { viewModel.navigateTo(CurrentScreen.HOME) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "CHAPTER MAP",
                        color = Color(0xFF94A3B8),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "SELECT LEVEL",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }

                // Total Stars Pill
                Row(
                    modifier = Modifier
                        .background(Color(0x1AFFFFFF), CircleShape)
                        .border(1.dp, Color(0x1AFFFFFF), CircleShape)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Stars",
                        tint = Color(0xFFFBBF24),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "$totalStars",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }

            // Chapter Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedChapterIndex,
                containerColor = Color.Transparent,
                contentColor = Color(0xFF3B82F6),
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedChapterIndex]),
                        color = Color(0xFF3B82F6),
                        height = 3.dp
                    )
                },
                divider = {}
            ) {
                GAME_CHAPTERS.forEachIndexed { index, chapter ->
                    val isChapterLocked = totalStars < chapter.requiredStars
                    Tab(
                        selected = selectedChapterIndex == index,
                        onClick = { selectedChapterIndex = index },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (isChapterLocked) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Locked",
                                        tint = Color(0x88FFFFFF),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                                Text(
                                    text = "Ch ${chapter.id}: ${chapter.title}",
                                    fontWeight = if (selectedChapterIndex == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedChapterIndex == index) Color(0xFF60A5FA) else Color(0xAAFFFFFF),
                                    fontSize = 13.sp
                                )
                            }
                        }
                    )
                }
            }

            // Chapter Info Header (Glass Card)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .background(Color(0x0DFFFFFF), RoundedCornerShape(18.dp))
                    .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(18.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Text(
                        text = currentChapter.title,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = currentChapter.description,
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp
                    )
                    if (totalStars < currentChapter.requiredStars) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "🔒 Requires ${currentChapter.requiredStars} stars to unlock",
                            color = Color(0xFFF87171),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Level Grid (4 columns)
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(chapterLevels) { progress ->
                    val isChapterLocked = totalStars < currentChapter.requiredStars
                    val isLevelAccessible = progress.isUnlocked && !isChapterLocked

                    val cardBg = when {
                        progress.isCompleted -> Color(0x1A10B981) // Soft green glass
                        isLevelAccessible -> Color(0x1A3B82F6) // Soft blue glass
                        else -> Color(0x08FFFFFF) // Dim locked
                    }

                    val cardBorder = when {
                        progress.isCompleted -> Color(0x4D10B981)
                        isLevelAccessible -> Color(0x4D3B82F6)
                        else -> Color(0x0DFFFFFF)
                    }

                    Box(
                        modifier = Modifier
                            .testTag("level_card_${progress.levelNumber}")
                            .height(86.dp)
                            .background(cardBg, RoundedCornerShape(16.dp))
                            .border(1.dp, cardBorder, RoundedCornerShape(16.dp))
                            .clickable(enabled = isLevelAccessible) {
                                viewModel.startLevel(progress.levelNumber)
                            }
                            .padding(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            if (isLevelAccessible) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${progress.levelNumber}",
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    if (progress.isPerfectSort) {
                                        Text(text = "👑", fontSize = 11.sp)
                                    }
                                }
                            } else {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Locked",
                                        tint = Color(0x40FFFFFF),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            // Bottom: Stars rating (1-3)
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                repeat(3) { starIdx ->
                                    val isFilled = starIdx < progress.stars
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Star",
                                        tint = if (isFilled) Color(0xFFFBBF24) else Color(0x26FFFFFF),
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
