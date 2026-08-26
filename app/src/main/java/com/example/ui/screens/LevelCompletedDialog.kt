package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.game.CurrentScreen
import com.example.game.GameplayUiState
import com.example.game.GameViewModel
import com.example.ui.components.CelebrationParticleEffect

@Composable
fun LevelCompletedDialog(
    viewModel: GameViewModel,
    state: GameplayUiState
) {
    var animateStars by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        animateStars = true
    }

    val starScale1 by animateFloatAsState(
        targetValue = if (animateStars && state.starsEarned >= 1) 1f else 0f,
        animationSpec = tween(durationMillis = 350, delayMillis = 100, easing = FastOutSlowInEasing),
        label = "star1"
    )

    val starScale2 by animateFloatAsState(
        targetValue = if (animateStars && state.starsEarned >= 2) 1.2f else 0f,
        animationSpec = tween(durationMillis = 350, delayMillis = 300, easing = FastOutSlowInEasing),
        label = "star2"
    )

    val starScale3 by animateFloatAsState(
        targetValue = if (animateStars && state.starsEarned >= 3) 1f else 0f,
        animationSpec = tween(durationMillis = 350, delayMillis = 500, easing = FastOutSlowInEasing),
        label = "star3"
    )

    Dialog(onDismissRequest = {}) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Confetti Burst Overlay
            CelebrationParticleEffect(modifier = Modifier.matchParentSize())

            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(24.dp))
                    .padding(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "LEVEL COMPLETE",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )

                    Text(
                        text = "VICTORY!",
                        color = Color(0xFFFBBF24),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 3 Stars Display with dynamic scaling
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Star 1",
                            tint = if (state.starsEarned >= 1) Color(0xFFFBBF24) else Color(0x26FFFFFF),
                            modifier = Modifier
                                .size(38.dp)
                                .scale(starScale1)
                        )
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Star 2",
                            tint = if (state.starsEarned >= 2) Color(0xFFFBBF24) else Color(0x26FFFFFF),
                            modifier = Modifier
                                .size(50.dp)
                                .scale(starScale2)
                        )
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Star 3",
                            tint = if (state.starsEarned >= 3) Color(0xFFFBBF24) else Color(0x26FFFFFF),
                            modifier = Modifier
                                .size(38.dp)
                                .scale(starScale3)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Perfect Sort Badge
                    if (state.isPerfectSort) {
                        Box(
                            modifier = Modifier
                                .background(Color(0x1A3B82F6), CircleShape)
                                .border(1.dp, Color(0x4D3B82F6), CircleShape)
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "👑", fontSize = 13.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "PERFECT SORT • BONUS EARNED",
                                    color = Color(0xFF93C5FD),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Level Statistics Glass Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0x0DFFFFFF), RoundedCornerShape(18.dp))
                            .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(18.dp))
                            .padding(14.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Moves Used", color = Color(0xFF94A3B8), fontSize = 13.sp)
                                Text(
                                    text = "${state.movesCount} / Target: ${state.levelConfig?.targetMoves ?: 0}",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Time Elapsed", color = Color(0xFF94A3B8), fontSize = 13.sp)
                                Text(
                                    text = "${state.elapsedTimeSeconds}s",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Coins Reward", color = Color(0xFF94A3B8), fontSize = 13.sp)
                                Text(
                                    text = "+${state.coinsEarned} 🪙",
                                    color = Color(0xFFFBBF24),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Next Level Button (Electric Blue)
                    Button(
                        onClick = { viewModel.nextLevel() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .testTag("next_level_button")
                            .fillMaxWidth()
                            .height(54.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "NEXT LEVEL",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Next Level",
                                tint = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Replay & Levels Actions (Glass pills)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .background(Color(0x0DFFFFFF), RoundedCornerShape(14.dp))
                                .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(14.dp))
                                .clickable { viewModel.restartCurrentLevel() },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Replay",
                                    tint = Color(0xFF94A3B8),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "Replay", color = Color(0xFFCBD5E1), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .background(Color(0x0DFFFFFF), RoundedCornerShape(14.dp))
                                .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(14.dp))
                                .clickable { viewModel.navigateTo(CurrentScreen.LEVEL_SELECT) },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.List,
                                    contentDescription = "Levels",
                                    tint = Color(0xFF94A3B8),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "Levels", color = Color(0xFFCBD5E1), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }
        }
    }
}

