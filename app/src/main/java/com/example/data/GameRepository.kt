package com.example.data

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class GameRepository(context: Context) {

    private val db = Room.databaseBuilder(
        context.applicationContext,
        GameDatabase::class.java,
        "colour_jar_fill.db"
    ).build()

    private val dao = db.gameDao()

    val levelProgressFlow: Flow<List<LevelProgressEntity>> = dao.getAllLevelProgress()
    val gameStatsFlow: Flow<GameStatsEntity?> = dao.getGameStats()

    suspend fun initializeIfEmpty() {
        val existingStats = dao.getGameStatsDirect()
        if (existingStats == null) {
            dao.insertOrUpdateStats(GameStatsEntity())
        }

        val allLevels = dao.getAllLevelProgress().firstOrNull()
        if (allLevels.isNullOrEmpty()) {
            val initialList = (1..50).map { lvl ->
                LevelProgressEntity(
                    levelNumber = lvl,
                    isUnlocked = (lvl == 1),
                    isCompleted = false,
                    stars = 0
                )
            }
            dao.insertAllLevels(initialList)
        }
    }

    suspend fun saveLevelCompletion(
        level: Int,
        stars: Int,
        moves: Int,
        timeSeconds: Int,
        isPerfectSort: Boolean,
        coinsEarned: Int
    ) {
        val current = dao.getLevelProgress(level)
        val bestStars = maxOf(current?.stars ?: 0, stars)
        val bestMoves = if (current?.bestMoves != null && current.bestMoves > 0) minOf(current.bestMoves, moves) else moves
        val bestTime = if (current?.bestTimeSeconds != null && current.bestTimeSeconds > 0) minOf(current.bestTimeSeconds, timeSeconds) else timeSeconds
        val perfect = (current?.isPerfectSort == true) || isPerfectSort

        dao.insertOrUpdateLevelProgress(
            LevelProgressEntity(
                levelNumber = level,
                isUnlocked = true,
                isCompleted = true,
                stars = bestStars,
                bestMoves = bestMoves,
                bestTimeSeconds = bestTime,
                isPerfectSort = perfect
            )
        )

        // Unlock next level
        val nextLevel = level + 1
        if (nextLevel <= 50) {
            val nextProgress = dao.getLevelProgress(nextLevel)
            if (nextProgress == null || !nextProgress.isUnlocked) {
                dao.insertOrUpdateLevelProgress(
                    LevelProgressEntity(
                        levelNumber = nextLevel,
                        isUnlocked = true,
                        isCompleted = nextProgress?.isCompleted ?: false,
                        stars = nextProgress?.stars ?: 0
                    )
                )
            }
        }

        // Update overall stats
        val stats = dao.getGameStatsDirect() ?: GameStatsEntity()
        val allCompleted = dao.getAllLevelProgress().firstOrNull()?.filter { it.isCompleted } ?: emptyList()
        val totalStars = allCompleted.sumOf { it.stars }
        val perfectCount = allCompleted.count { it.isPerfectSort }

        val newHighest = maxOf(stats.highestUnlockedLevel, nextLevel.coerceAtMost(50))
        val newStats = stats.copy(
            totalCoins = stats.totalCoins + coinsEarned,
            totalLevelsCompleted = allCompleted.size,
            totalStarsCollected = totalStars,
            totalPerfectSorts = perfectCount,
            highestUnlockedLevel = newHighest,
            currentLevel = nextLevel.coerceAtMost(50)
        )
        dao.insertOrUpdateStats(newStats)
    }

    suspend fun updateStats(stats: GameStatsEntity) {
        dao.insertOrUpdateStats(stats)
    }

    suspend fun unlockSkin(skinId: String, cost: Int) {
        val stats = dao.getGameStatsDirect() ?: return
        if (stats.totalCoins >= cost) {
            val currentUnlocked = stats.unlockedJarSkins.split(",").toMutableSet()
            currentUnlocked.add(skinId)
            dao.insertOrUpdateStats(
                stats.copy(
                    totalCoins = stats.totalCoins - cost,
                    unlockedJarSkins = currentUnlocked.joinToString(","),
                    selectedJarSkinId = skinId
                )
            )
        }
    }

    suspend fun unlockTheme(themeId: String, cost: Int) {
        val stats = dao.getGameStatsDirect() ?: return
        if (stats.totalCoins >= cost) {
            val currentUnlocked = stats.unlockedBgThemes.split(",").toMutableSet()
            currentUnlocked.add(themeId)
            dao.insertOrUpdateStats(
                stats.copy(
                    totalCoins = stats.totalCoins - cost,
                    unlockedBgThemes = currentUnlocked.joinToString(","),
                    selectedBgThemeId = themeId
                )
            )
        }
    }

    suspend fun buyPowerUp(type: com.example.model.PowerUpType) {
        val stats = dao.getGameStatsDirect() ?: return
        if (stats.totalCoins >= type.coinCost) {
            val newStats = when (type) {
                com.example.model.PowerUpType.EXTRA_TUBE -> stats.copy(
                    totalCoins = stats.totalCoins - type.coinCost,
                    extraTubesCount = stats.extraTubesCount + 1
                )
                com.example.model.PowerUpType.HINT -> stats.copy(
                    totalCoins = stats.totalCoins - type.coinCost,
                    hintsCount = stats.hintsCount + 1
                )
                com.example.model.PowerUpType.SWAP_TOKEN -> stats.copy(
                    totalCoins = stats.totalCoins - type.coinCost,
                    swapTokensCount = stats.swapTokensCount + 1
                )
                com.example.model.PowerUpType.TIME_CRYSTAL -> stats.copy(
                    totalCoins = stats.totalCoins - type.coinCost,
                    timeCrystalsCount = stats.timeCrystalsCount + 1
                )
                com.example.model.PowerUpType.RAINBOW_SPLASH -> stats.copy(
                    totalCoins = stats.totalCoins - type.coinCost
                )
            }
            dao.insertOrUpdateStats(newStats)
        }
    }
}
