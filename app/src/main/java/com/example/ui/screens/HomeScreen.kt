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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.model.ColorSegment
import com.example.model.Jar
import com.example.model.JarColor
import com.example.ui.components.LiquidJarView

@Composable
fun HomeScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val stats by viewModel.gameStats.collectAsState()
    val currentLevel = stats?.currentLevel ?: 1
    val totalCoins = stats?.totalCoins ?: 0
    val totalStars = stats?.totalStarsCollected ?: 0
    val activeSkin = viewModel.getSelectedJarSkin()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0x331E3A8A), // Radial top blue glow
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
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Bar: Coins & Stars Glass Pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Stars Badge: bg-white/10 px-3 py-1.5 rounded-full border border-white/10
                Row(
                    modifier = Modifier
                        .background(Color(0x1AFFFFFF), CircleShape)
                        .border(1.dp, Color(0x1AFFFFFF), CircleShape)
                        .padding(horizontal = 14.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Stars",
                        tint = Color(0xFFFBBF24),
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$totalStars",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp
                    )
                }

                // Coins Badge: bg-white/10 px-3 py-1.5 rounded-full border border-white/10
                Row(
                    modifier = Modifier
                        .background(Color(0x1AFFFFFF), CircleShape)
                        .border(1.dp, Color(0x1AFFFFFF), CircleShape)
                        .clickable { viewModel.navigateTo(CurrentScreen.SHOP) }
                        .padding(horizontal = 14.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🪙", fontSize = 15.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$totalCoins",
                        color = Color(0xFFFBBF24),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Game Logo / Hero Title
            Text(
                text = "COLOUR JAR FILL",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp
            )
            Text(
                text = "LIQUID SORTING MASTER",
                color = Color(0xFF94A3B8),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 3.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Hero Visual Glass Showcase
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x0DFFFFFF), RoundedCornerShape(24.dp))
                    .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(24.dp))
                    .padding(vertical = 20.dp, horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LiquidJarView(
                            jar = Jar(
                                id = 1,
                                segments = listOf(
                                    ColorSegment(JarColor.PURPLE_NEON),
                                    ColorSegment(JarColor.CYAN_TEAL),
                                    ColorSegment(JarColor.AMBER_GOLD),
                                    ColorSegment(JarColor.HOT_PINK)
                                )
                            ),
                            jarSkin = activeSkin,
                            isColorblindMode = false,
                            onJarClick = {},
                            width = 54.dp,
                            height = 140.dp
                        )

                        LiquidJarView(
                            jar = Jar(
                                id = 2,
                                segments = listOf(
                                    ColorSegment(JarColor.CORAL_RED),
                                    ColorSegment(JarColor.CORAL_RED),
                                    ColorSegment(JarColor.CORAL_RED),
                                    ColorSegment(JarColor.CORAL_RED)
                                ),
                                isCompleted = true
                            ),
                            jarSkin = activeSkin,
                            isColorblindMode = false,
                            onJarClick = {},
                            width = 54.dp,
                            height = 140.dp
                        )

                        LiquidJarView(
                            jar = Jar(
                                id = 3,
                                segments = listOf(
                                    ColorSegment(JarColor.EMERALD_GREEN),
                                    ColorSegment(JarColor.OCEAN_BLUE),
                                    ColorSegment(JarColor.MINT_ICE)
                                )
                            ),
                            jarSkin = activeSkin,
                            isColorblindMode = false,
                            onJarClick = {},
                            width = 54.dp,
                            height = 140.dp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "5 Chapters • 50 Handcrafted Levels • Fluid Dynamics",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            // Primary PLAY Button (Electric Blue / Cyan glow)
            Button(
                onClick = { viewModel.startLevel(currentLevel) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .testTag("play_button")
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "PLAY LEVEL $currentLevel",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Secondary Buttons Grid (Immersive UI Glass Cards)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Level Select Button
                Box(
                    modifier = Modifier
                        .testTag("level_select_button")
                        .weight(1f)
                        .height(56.dp)
                        .background(Color(0x0DFFFFFF), RoundedCornerShape(18.dp))
                        .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(18.dp))
                        .clickable { viewModel.navigateTo(CurrentScreen.LEVEL_SELECT) },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = "🗺️", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Levels",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                }

                // Theme Shop Button
                Box(
                    modifier = Modifier
                        .testTag("shop_button")
                        .weight(1f)
                        .height(56.dp)
                        .background(Color(0x0DFFFFFF), RoundedCornerShape(18.dp))
                        .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(18.dp))
                        .clickable { viewModel.navigateTo(CurrentScreen.SHOP) },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = "Shop",
                            tint = Color(0xFF60A5FA),
                            modifier = Modifier.size(19.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Skins",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Statistics Button
                Box(
                    modifier = Modifier
                        .testTag("stats_button")
                        .weight(1f)
                        .height(54.dp)
                        .background(Color(0x0DFFFFFF), RoundedCornerShape(18.dp))
                        .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(18.dp))
                        .clickable { viewModel.navigateTo(CurrentScreen.STATS) },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = "Stats",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(19.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Stats",
                            color = Color(0xFFCBD5E1),
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                }

                // Settings Button
                Box(
                    modifier = Modifier
                        .testTag("settings_button")
                        .weight(1f)
                        .height(54.dp)
                        .background(Color(0x0DFFFFFF), RoundedCornerShape(18.dp))
                        .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(18.dp))
                        .clickable { viewModel.navigateTo(CurrentScreen.SETTINGS) },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(19.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Settings",
                            color = Color(0xFFCBD5E1),
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Rewarded Free Coins Booster card (Immersive Glass card)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x0DFFFFFF), RoundedCornerShape(18.dp))
                    .border(1.dp, Color(0x1AFBBF24), RoundedCornerShape(18.dp))
                    .clickable { viewModel.claimRewardedAdBonus() }
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🎁", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Daily Bonus Reward",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp
                            )
                            Text(
                                text = "+100 Coins & +1 Free Hint",
                                color = Color(0xFFFBBF24),
                                fontSize = 11.5.sp
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFFBBF24), CircleShape)
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "CLAIM",
                            color = Color(0xFF0F172A),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

