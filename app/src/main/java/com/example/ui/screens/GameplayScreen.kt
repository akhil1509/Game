package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.GameViewModel
import com.example.model.Jar
import com.example.ui.components.LiquidJarView

@Composable
fun GameplayScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.gameplayState.collectAsState()
    val stats by viewModel.gameStats.collectAsState()
    val isColorblind = stats?.colorblindMode ?: false
    val activeSkin = viewModel.getSelectedJarSkin()
    val totalStars = stats?.totalStarsCollected ?: 0

    val infiniteTransition = rememberInfiniteTransition(label = "immersiveEffects")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseDot"
    )

    val comboPulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "comboPulse"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0x331E3A8A), // radial/top blue glow
                        Color(0x1A0F172A),
                        Color(0xFF0F172A)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. IMMERSIVE UI HEADER (Pause Button, Level Title, Star Score Pill)
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Pause Button: w-11 h-11 bg-white/10 rounded-2xl border border-white/10
                    Box(
                        modifier = Modifier
                            .testTag("pause_button")
                            .size(44.dp)
                            .background(Color(0x1AFFFFFF), RoundedCornerShape(16.dp))
                            .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(16.dp))
                            .clickable { viewModel.pauseGame() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Pause,
                            contentDescription = "Pause",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Level Center Badge
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "LEVEL",
                            color = Color(0xFF94A3B8),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                        Text(
                            text = "${state.currentLevelNumber}",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.5).sp
                        )
                    }

                    // Star Score Pill: bg-white/10 px-3 py-1.5 rounded-full border border-white/10
                    Row(
                        modifier = Modifier
                            .background(Color(0x1AFFFFFF), CircleShape)
                            .border(1.dp, Color(0x1AFFFFFF), CircleShape)
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Stars",
                            tint = Color(0xFFFBBF24),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (state.score > 0) "${state.score}" else "$totalStars",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // 2. SUBTITLE BADGE: Moves Remaining with Blue Animated Pulse Dot
                val targetMoves = state.levelConfig?.targetMoves ?: 10
                val remainingMoves = (targetMoves - state.movesCount).coerceAtLeast(0)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Row(
                        modifier = Modifier
                            .background(Color(0x0DFFFFFF), CircleShape)
                            .border(1.dp, Color(0x1AFFFFFF), CircleShape)
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Pulsing Blue Dot
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .scale(1f + (pulseAlpha * 0.3f))
                                .background(Color(0xFF60A5FA).copy(alpha = pulseAlpha), CircleShape)
                        )

                        Text(
                            text = "Moves: ",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${state.movesCount} / $targetMoves",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )

                        if (state.timeLimitRemainingSeconds > 0) {
                            Text(
                                text = "• ⏳ ${state.timeLimitRemainingSeconds}s",
                                color = Color(0xFFFBBF24),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Combo badge if active
                if (state.comboCount > 1) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .scale(comboPulse)
                                .background(Color(0xFF2563EB), CircleShape)
                                .padding(horizontal = 12.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "🔥 ${state.comboCount}x COMBO",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }

                // Swap Mode active banner
                if (state.isSwapModeActive) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0x332563EB), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0x662563EB), RoundedCornerShape(12.dp))
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🔄 SWAP MODE: Tap two jars to exchange their positions",
                            color = Color(0xFF93C5FD),
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // 3. CENTER PLAYFIELD (Jars Grid with Immersive Glass tubes)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                JarsPlayfield(
                    jars = state.jars,
                    activeSkin = activeSkin,
                    isColorblind = isColorblind,
                    activeHint = state.activeHint,
                    onJarClick = { jarId -> viewModel.onJarTapped(jarId) }
                )
            }

            // 4. IMMERSIVE UI FOOTER CONTROLS (Glass Cards grid)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Undo Button
                    val canUndo = state.undoHistory.isNotEmpty() && !state.isLevelCompleted
                    ImmersivePowerUpButton(
                        modifier = Modifier.weight(1f),
                        icon = "↩",
                        title = "Undo",
                        badge = if (state.undoHistory.isNotEmpty()) "${state.undoHistory.size}" else "",
                        isEnabled = canUndo,
                        onClick = { viewModel.undoLastMove() },
                        testTag = "undo_button"
                    )

                    // Extra Tube Booster
                    val extraTubes = stats?.extraTubesCount ?: 0
                    ImmersivePowerUpButton(
                        modifier = Modifier.weight(1f),
                        icon = "+",
                        title = "Tube",
                        badge = "$extraTubes",
                        isEnabled = extraTubes > 0 && !state.isLevelCompleted,
                        onClick = { viewModel.useExtraTube() },
                        testTag = "extra_tube_button"
                    )

                    // Smart Hint (Highlighted Primary)
                    val hints = stats?.hintsCount ?: 0
                    ImmersivePowerUpButton(
                        modifier = Modifier.weight(1f),
                        icon = "💡",
                        title = "Hint",
                        badge = "$hints",
                        isEnabled = hints > 0 && !state.isLevelCompleted,
                        isPrimary = true,
                        onClick = { viewModel.useHint() },
                        testTag = "hint_button"
                    )

                    // Swap Token
                    val swaps = stats?.swapTokensCount ?: 0
                    ImmersivePowerUpButton(
                        modifier = Modifier.weight(1f),
                        icon = "🔄",
                        title = "Swap",
                        badge = "$swaps",
                        isEnabled = swaps > 0 && !state.isLevelCompleted,
                        onClick = { viewModel.useSwapToken() },
                        testTag = "swap_button"
                    )

                    // Time Crystal / Freeze
                    val crystals = stats?.timeCrystalsCount ?: 0
                    ImmersivePowerUpButton(
                        modifier = Modifier.weight(1f),
                        icon = "⏳",
                        title = "Freeze",
                        badge = "$crystals",
                        isEnabled = crystals > 0 && !state.isLevelCompleted,
                        onClick = { viewModel.useTimeCrystal() },
                        testTag = "freeze_button"
                    )
                }
            }
        }

        // Dialog Overlays
        if (state.isPaused) {
            PauseDialog(
                viewModel = viewModel,
                levelNumber = state.currentLevelNumber,
                onDismiss = { viewModel.resumeGame() }
            )
        }

        if (state.isLevelCompleted) {
            LevelCompletedDialog(
                viewModel = viewModel,
                state = state
            )
        }
    }
}

@Composable
private fun JarsPlayfield(
    jars: List<Jar>,
    activeSkin: com.example.model.JarSkinTheme,
    isColorblind: Boolean,
    activeHint: com.example.solver.JarSolver.Move?,
    onJarClick: (Int) -> Unit
) {
    val totalJars = jars.size
    val (topRowJars, bottomRowJars) = if (totalJars <= 4) {
        Pair(jars, emptyList())
    } else {
        val half = (totalJars + 1) / 2
        Pair(jars.take(half), jars.drop(half))
    }

    val jarWidth = when {
        totalJars <= 4 -> 72.dp
        totalJars <= 6 -> 66.dp
        totalJars <= 8 -> 58.dp
        else -> 52.dp
    }

    val jarHeight = when {
        totalJars <= 4 -> 205.dp
        totalJars <= 6 -> 180.dp
        else -> 160.dp
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        // Top Row
        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            topRowJars.forEach { jar ->
                val isHintFrom = (activeHint?.fromId == jar.id)
                val isHintTo = (activeHint?.toId == jar.id)

                Box(contentAlignment = Alignment.TopCenter) {
                    LiquidJarView(
                        jar = jar,
                        jarSkin = activeSkin,
                        isColorblindMode = isColorblind,
                        onJarClick = { onJarClick(jar.id) },
                        width = jarWidth,
                        height = jarHeight
                    )

                    if (isHintFrom) {
                        Text(
                            text = "POUR 👆",
                            color = Color(0xFFFBBF24),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier
                                .background(Color(0xE60F172A), CircleShape)
                                .border(1.dp, Color(0x4DFBBF24), CircleShape)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                    if (isHintTo) {
                        Text(
                            text = "HERE 🎯",
                            color = Color(0xFF00F5D4),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier
                                .background(Color(0xE60F172A), CircleShape)
                                .border(1.dp, Color(0x4D00F5D4), CircleShape)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        // Bottom Row (if multi-row)
        if (bottomRowJars.isNotEmpty()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                bottomRowJars.forEach { jar ->
                    val isHintFrom = (activeHint?.fromId == jar.id)
                    val isHintTo = (activeHint?.toId == jar.id)

                    Box(contentAlignment = Alignment.TopCenter) {
                        LiquidJarView(
                            jar = jar,
                            jarSkin = activeSkin,
                            isColorblindMode = isColorblind,
                            onJarClick = { onJarClick(jar.id) },
                            width = jarWidth,
                            height = jarHeight
                        )

                        if (isHintFrom) {
                            Text(
                                text = "POUR 👆",
                                color = Color(0xFFFBBF24),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier
                                .background(Color(0xE60F172A), CircleShape)
                                .border(1.dp, Color(0x4DFBBF24), CircleShape)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                        if (isHintTo) {
                            Text(
                                text = "HERE 🎯",
                                color = Color(0xFF00F5D4),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier
                                .background(Color(0xE60F172A), CircleShape)
                                .border(1.dp, Color(0x4D00F5D4), CircleShape)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Immersive UI Glass Control Card:
 * bg-white/5 hover:bg-white/10 rounded-2xl border border-white/10
 */
@Composable
private fun ImmersivePowerUpButton(
    modifier: Modifier = Modifier,
    icon: String,
    title: String,
    badge: String,
    isEnabled: Boolean,
    isPrimary: Boolean = false,
    onClick: () -> Unit,
    testTag: String
) {
    val bgColor = when {
        isPrimary && isEnabled -> Color(0xFF2563EB)
        isEnabled -> Color(0x0DFFFFFF) // bg-white/5
        else -> Color(0x08FFFFFF)
    }

    val borderColor = when {
        isPrimary && isEnabled -> Color(0x663B82F6)
        isEnabled -> Color(0x1AFFFFFF) // border-white/10
        else -> Color(0x0DFFFFFF)
    }

    Box(
        modifier = modifier
            .testTag(testTag)
            .height(64.dp)
            .background(bgColor, RoundedCornerShape(16.dp))
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(enabled = isEnabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(4.dp)
        ) {
            Box(contentAlignment = Alignment.TopEnd) {
                Text(
                    text = icon,
                    fontSize = 18.sp,
                    color = if (isEnabled) Color.White else Color(0x66FFFFFF)
                )

                if (badge.isNotEmpty() && badge != "0") {
                    Box(
                        modifier = Modifier
                            .offset(x = 10.dp, y = (-4).dp)
                            .background(Color(0xFF0F172A), CircleShape)
                            .border(1.dp, Color(0x4DFBBF24), CircleShape)
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = badge,
                            color = Color(0xFFFBBF24),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = title,
                color = if (isEnabled) {
                    if (isPrimary) Color.White else Color(0xFF94A3B8)
                } else {
                    Color(0x4094A3B8)
                },
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

