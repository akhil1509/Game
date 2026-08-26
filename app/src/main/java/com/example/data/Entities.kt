package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "level_progress")
data class LevelProgressEntity(
    @PrimaryKey val levelNumber: Int,
    val isUnlocked: Boolean = false,
    val isCompleted: Boolean = false,
    val stars: Int = 0,
    val bestMoves: Int = 0,
    val bestTimeSeconds: Int = 0,
    val isPerfectSort: Boolean = false
)

@Entity(tableName = "game_stats")
data class GameStatsEntity(
    @PrimaryKey val id: Int = 1,
    val totalCoins: Int = 200, // Starter bonus
    val currentLevel: Int = 1,
    val highestUnlockedLevel: Int = 1,
    val totalGamesPlayed: Int = 0,
    val totalLevelsCompleted: Int = 0,
    val totalPours: Int = 0,
    val totalPerfectSorts: Int = 0,
    val maxComboAchieved: Int = 0,
    val totalStarsCollected: Int = 0,
    val totalPlayTimeSeconds: Long = 0L,
    val hintsCount: Int = 5,
    val extraTubesCount: Int = 3,
    val swapTokensCount: Int = 2,
    val timeCrystalsCount: Int = 2,
    val selectedJarSkinId: String = "classic",
    val selectedBgThemeId: String = "deep_indigo",
    val unlockedJarSkins: String = "classic", // Comma-separated
    val unlockedBgThemes: String = "deep_indigo",
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val colorblindMode: Boolean = false,
    val hasRemovedAds: Boolean = false
)
