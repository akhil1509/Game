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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
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
import com.example.model.AVAILABLE_BG_THEMES
import com.example.model.AVAILABLE_JAR_SKINS
import com.example.model.BackgroundTheme
import com.example.model.ColorSegment
import com.example.model.Jar
import com.example.model.JarColor
import com.example.model.JarSkinTheme
import com.example.model.PowerUpType
import com.example.ui.components.LiquidJarView

@Composable
fun ShopScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val stats by viewModel.gameStats.collectAsState()
    val totalCoins = stats?.totalCoins ?: 0
    val selectedJarSkinId = stats?.selectedJarSkinId ?: "classic"
    val selectedBgThemeId = stats?.selectedBgThemeId ?: "deep_indigo"
    val unlockedSkins = (stats?.unlockedJarSkins ?: "classic").split(",")
    val unlockedThemes = (stats?.unlockedBgThemes ?: "deep_indigo").split(",")

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Jar Skins", "Themes", "Power-Ups", "Special")

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
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Back Button
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
                        text = "STORE",
                        color = Color(0xFF94A3B8),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "CUSTOMIZE & BOOST",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }

                // Coins Pill
                Row(
                    modifier = Modifier
                        .background(Color(0x1AFFFFFF), CircleShape)
                        .border(1.dp, Color(0x1AFFFFFF), CircleShape)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = "🪙", fontSize = 13.sp)
                    Text(
                        text = "$totalCoins",
                        color = Color(0xFFFBBF24),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }

            // Tab Bar
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = Color(0xFF3B82F6),
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        color = Color(0xFF3B82F6),
                        height = 3.dp
                    )
                },
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) Color(0xFF60A5FA) else Color(0xAAFFFFFF),
                                fontSize = 13.sp
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tab Content
            when (selectedTab) {
                0 -> {
                    // Jar Skins Tab
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(AVAILABLE_JAR_SKINS) { skin ->
                            val isUnlocked = unlockedSkins.contains(skin.id)
                            val isSelected = selectedJarSkinId == skin.id

                            JarSkinShopCard(
                                skin = skin,
                                isUnlocked = isUnlocked,
                                isSelected = isSelected,
                                userCoins = totalCoins,
                                onSelectOrBuy = { viewModel.unlockOrSelectSkin(skin) }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(20.dp)) }
                    }
                }
                1 -> {
                    // Background Themes Tab
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(AVAILABLE_BG_THEMES) { theme ->
                            val isUnlocked = unlockedThemes.contains(theme.id)
                            val isSelected = selectedBgThemeId == theme.id

                            BgThemeShopCard(
                                theme = theme,
                                isUnlocked = isUnlocked,
                                isSelected = isSelected,
                                userCoins = totalCoins,
                                onSelectOrBuy = { viewModel.unlockOrSelectTheme(theme) }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(20.dp)) }
                    }
                }
                2 -> {
                    // Power-Ups Tab
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(PowerUpType.entries) { powerUp ->
                            PowerUpShopCard(
                                powerUp = powerUp,
                                userCoins = totalCoins,
                                onBuy = { viewModel.buyPowerUp(powerUp) }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(20.dp)) }
                    }
                }
                3 -> {
                    // Special / Bonus Tab
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Free Coins Daily Bonus
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0x0DFFFFFF), RoundedCornerShape(20.dp))
                                .border(1.dp, Color(0x33FBBF24), RoundedCornerShape(20.dp))
                                .padding(16.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "🎁", fontSize = 28.sp)
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = "Bonus Supply Drop",
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp
                                            )
                                            Text(
                                                text = "+100 Coins • +1 Hint • +1 Tube",
                                                color = Color(0xFFFBBF24),
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(14.dp))
                                Button(
                                    onClick = { viewModel.claimRewardedAdBonus() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFBBF24)),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier.fillMaxWidth().height(46.dp)
                                ) {
                                    Text(
                                        text = "CLAIM FREE REWARD",
                                        color = Color(0xFF0F172A),
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }

                        // Remove Ads Option
                        val hasRemovedAds = stats?.hasRemovedAds == true
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0x0DFFFFFF), RoundedCornerShape(20.dp))
                                .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(20.dp))
                                .padding(16.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "🚫", fontSize = 28.sp)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "Ad-Free Experience",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        )
                                        Text(
                                            text = if (hasRemovedAds) "Active • Permanent Ad-Free" else "Enjoy uninterrupted zen puzzle solving",
                                            color = Color(0xFF94A3B8),
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(14.dp))
                                if (hasRemovedAds) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(46.dp)
                                            .background(Color(0x1A10B981), RoundedCornerShape(14.dp))
                                            .border(1.dp, Color(0x3310B981), RoundedCornerShape(14.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "✓ UNLOCKED",
                                            color = Color(0xFF34D399),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    }
                                } else {
                                    Button(
                                        onClick = { viewModel.removeAds() },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                                        shape = RoundedCornerShape(14.dp),
                                        modifier = Modifier.fillMaxWidth().height(46.dp)
                                    ) {
                                        Text(
                                            text = "UNLOCK AD-FREE",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
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
}

@Composable
private fun JarSkinShopCard(
    skin: JarSkinTheme,
    isUnlocked: Boolean,
    isSelected: Boolean,
    userCoins: Int,
    onSelectOrBuy: () -> Unit
) {
    val sampleJar = remember {
        Jar(
            id = 1,
            segments = listOf(
                ColorSegment(JarColor.CORAL_RED),
                ColorSegment(JarColor.OCEAN_BLUE),
                ColorSegment(JarColor.AMBER_GOLD),
                ColorSegment(JarColor.CYAN_TEAL)
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isSelected) Color(0x1A3B82F6) else Color(0x0DFFFFFF),
                RoundedCornerShape(18.dp)
            )
            .border(
                1.dp,
                if (isSelected) Color(0xFF3B82F6) else Color(0x1AFFFFFF),
                RoundedCornerShape(18.dp)
            )
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Preview jar
                Box(
                    modifier = Modifier
                        .size(width = 46.dp, height = 76.dp)
                        .background(Color(0x0DFFFFFF), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    LiquidJarView(
                        jar = sampleJar,
                        jarSkin = skin,
                        isColorblindMode = false,
                        onJarClick = {},
                        width = 36.dp,
                        height = 68.dp
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = skin.name,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = skin.description,
                        color = Color(0xFF94A3B8),
                        fontSize = 11.5.sp
                    )
                    if (!isUnlocked) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🪙", fontSize = 11.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${skin.costCoins} Coins",
                                color = Color(0xFFFBBF24),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Action Button
            when {
                isSelected -> {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF3B82F6), RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "EQUIPPED",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
                isUnlocked -> {
                    Box(
                        modifier = Modifier
                            .background(Color(0x1AFFFFFF), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(12.dp))
                            .clickable { onSelectOrBuy() }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "EQUIP",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
                else -> {
                    val canAfford = userCoins >= skin.costCoins
                    Box(
                        modifier = Modifier
                            .background(
                                if (canAfford) Color(0xFFFBBF24) else Color(0x1AFFFFFF),
                                RoundedCornerShape(12.dp)
                            )
                            .clickable(enabled = canAfford) { onSelectOrBuy() }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = if (canAfford) "BUY" else "LOCKED",
                            color = if (canAfford) Color(0xFF0F172A) else Color(0x66FFFFFF),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BgThemeShopCard(
    theme: BackgroundTheme,
    isUnlocked: Boolean,
    isSelected: Boolean,
    userCoins: Int,
    onSelectOrBuy: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isSelected) Color(0x1A3B82F6) else Color(0x0DFFFFFF),
                RoundedCornerShape(18.dp)
            )
            .border(
                1.dp,
                if (isSelected) Color(0xFF3B82F6) else Color(0x1AFFFFFF),
                RoundedCornerShape(18.dp)
            )
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Color swatch
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(theme.topColorHex), Color(theme.bottomColorHex))
                            ),
                            RoundedCornerShape(12.dp)
                        )
                        .border(1.dp, Color(theme.accentColorHex), RoundedCornerShape(12.dp))
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = theme.name,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    if (!isUnlocked) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🪙", fontSize = 11.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${theme.costCoins} Coins",
                                color = Color(0xFFFBBF24),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            when {
                isSelected -> {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF3B82F6), RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "ACTIVE",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
                isUnlocked -> {
                    Box(
                        modifier = Modifier
                            .background(Color(0x1AFFFFFF), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(12.dp))
                            .clickable { onSelectOrBuy() }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "APPLY",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
                else -> {
                    val canAfford = userCoins >= theme.costCoins
                    Box(
                        modifier = Modifier
                            .background(
                                if (canAfford) Color(0xFFFBBF24) else Color(0x1AFFFFFF),
                                RoundedCornerShape(12.dp)
                            )
                            .clickable(enabled = canAfford) { onSelectOrBuy() }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = if (canAfford) "BUY" else "LOCKED",
                            color = if (canAfford) Color(0xFF0F172A) else Color(0x66FFFFFF),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PowerUpShopCard(
    powerUp: PowerUpType,
    userCoins: Int,
    onBuy: () -> Unit
) {
    val canAfford = userCoins >= powerUp.coinCost

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0x0DFFFFFF), RoundedCornerShape(18.dp))
            .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(18.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(Color(0x1AFFFFFF), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = powerUp.icon, fontSize = 22.sp)
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = powerUp.displayName,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = powerUp.description,
                        color = Color(0xFF94A3B8),
                        fontSize = 11.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Box(
                modifier = Modifier
                    .background(
                        if (canAfford) Color(0xFF2563EB) else Color(0x1AFFFFFF),
                        RoundedCornerShape(12.dp)
                    )
                    .clickable(enabled = canAfford) { onBuy() }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🪙", fontSize = 11.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${powerUp.coinCost}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
