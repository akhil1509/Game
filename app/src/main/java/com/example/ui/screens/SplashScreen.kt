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
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.example.game.CurrentScreen
import com.example.game.GameViewModel
import com.example.model.AVAILABLE_JAR_SKINS
import com.example.model.ColorSegment
import com.example.model.Jar
import com.example.model.JarColor
import com.example.ui.components.LiquidJarView
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "splashAnim")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowScale"
    )

    LaunchedEffect(Unit) {
        delay(2200)
        viewModel.navigateTo(CurrentScreen.HOME)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0x4D1E3A8A),
                        Color(0x1A0F172A),
                        Color(0xFF0F172A)
                    )
                )
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                viewModel.navigateTo(CurrentScreen.HOME)
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Animated Sample Jars in a glass podium container
            Box(
                modifier = Modifier
                    .background(Color(0x0DFFFFFF), RoundedCornerShape(24.dp))
                    .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(24.dp))
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier.scale(glowScale)
                ) {
                    LiquidJarView(
                        jar = Jar(
                            id = 1,
                            segments = listOf(
                                ColorSegment(JarColor.CORAL_RED),
                                ColorSegment(JarColor.OCEAN_BLUE),
                                ColorSegment(JarColor.AMBER_GOLD),
                                ColorSegment(JarColor.CORAL_RED)
                            )
                        ),
                        jarSkin = AVAILABLE_JAR_SKINS.first(),
                        isColorblindMode = false,
                        onJarClick = {},
                        width = 54.dp,
                        height = 140.dp
                    )

                    LiquidJarView(
                        jar = Jar(
                            id = 2,
                            segments = listOf(
                                ColorSegment(JarColor.CYAN_TEAL),
                                ColorSegment(JarColor.EMERALD_GREEN),
                                ColorSegment(JarColor.PURPLE_NEON),
                                ColorSegment(JarColor.HOT_PINK)
                            ),
                            isSelected = true
                        ),
                        jarSkin = AVAILABLE_JAR_SKINS.first(),
                        isColorblindMode = false,
                        onJarClick = {},
                        width = 54.dp,
                        height = 140.dp
                    )

                    LiquidJarView(
                        jar = Jar(
                            id = 3,
                            segments = listOf(
                                ColorSegment(JarColor.AMBER_GOLD),
                                ColorSegment(JarColor.AMBER_GOLD),
                                ColorSegment(JarColor.AMBER_GOLD),
                                ColorSegment(JarColor.AMBER_GOLD)
                            ),
                            isCompleted = true
                        ),
                        jarSkin = AVAILABLE_JAR_SKINS.first(),
                        isColorblindMode = false,
                        onJarClick = {},
                        width = 54.dp,
                        height = 140.dp
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Glowing Title
            Text(
                text = "COLOUR JAR FILL",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Liquid Sorting Puzzle Masterpiece",
                color = Color(0xFF60A5FA),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(44.dp))

            // Tap to Start pill button
            Box(
                modifier = Modifier
                    .testTag("tap_to_start_button")
                    .background(Color(0x1AFFFFFF), CircleShape)
                    .border(1.dp, Color(0x33FFFFFF), CircleShape)
                    .clickable { viewModel.navigateTo(CurrentScreen.HOME) }
                    .padding(horizontal = 28.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "TAP TO PLAY ✨",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}
