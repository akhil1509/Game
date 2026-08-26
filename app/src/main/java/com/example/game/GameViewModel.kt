package com.example.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SoundManager
import com.example.data.GameRepository
import com.example.data.GameStatsEntity
import com.example.data.LevelProgressEntity
import com.example.model.AVAILABLE_BG_THEMES
import com.example.model.AVAILABLE_JAR_SKINS
import com.example.model.BackgroundTheme
import com.example.model.ColorSegment
import com.example.model.Jar
import com.example.model.JarSkinTheme
import com.example.model.JarType
import com.example.model.LevelConfig
import com.example.model.MoveHistory
import com.example.model.PowerUpType
import com.example.solver.JarSolver
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class CurrentScreen {
    SPLASH,
    HOME,
    LEVEL_SELECT,
    GAMEPLAY,
    STATS,
    SETTINGS,
    SHOP
}

data class GameplayUiState(
    val currentLevelNumber: Int = 1,
    val levelConfig: LevelConfig? = null,
    val jars: List<Jar> = emptyList(),
    val selectedJarId: Int? = null,
    val isPouring: Boolean = false,
    val pouringFromId: Int? = null,
    val pouringToId: Int? = null,
    val movesCount: Int = 0,
    val undoHistory: List<MoveHistory> = emptyList(),
    val isLevelCompleted: Boolean = false,
    val isPaused: Boolean = false,
    val starsEarned: Int = 0,
    val coinsEarned: Int = 0,
    val isPerfectSort: Boolean = true,
    val comboCount: Int = 0,
    val score: Int = 0,
    val activeHint: JarSolver.Move? = null,
    val elapsedTimeSeconds: Int = 0,
    val timeLimitRemainingSeconds: Int = 0,
    val showCelebrationParticles: Boolean = false,
    val isSwapModeActive: Boolean = false,
    val swapFirstJarId: Int? = null
)

class GameViewModel(application: Application) : AndroidViewModel(application) {

    val repository = GameRepository(application)
    val soundManager = SoundManager(application)

    private val _currentScreen = MutableStateFlow(CurrentScreen.SPLASH)
    val currentScreen: StateFlow<CurrentScreen> = _currentScreen.asStateFlow()

    val levelProgressList: StateFlow<List<LevelProgressEntity>> = repository.levelProgressFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val gameStats: StateFlow<GameStatsEntity?> = repository.gameStatsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _gameplayState = MutableStateFlow(GameplayUiState())
    val gameplayState: StateFlow<GameplayUiState> = _gameplayState.asStateFlow()

    private var timerJob: Job? = null
    private var comboDecayJob: Job? = null

    init {
        viewModelScope.launch {
            repository.initializeIfEmpty()
        }
    }

    fun navigateTo(screen: CurrentScreen) {
        _currentScreen.value = screen
    }

    fun getSelectedJarSkin(): JarSkinTheme {
        val stats = gameStats.value
        val skinId = stats?.selectedJarSkinId ?: "classic"
        return AVAILABLE_JAR_SKINS.find { it.id == skinId } ?: AVAILABLE_JAR_SKINS.first()
    }

    fun getSelectedBgTheme(): BackgroundTheme {
        val stats = gameStats.value
        val themeId = stats?.selectedBgThemeId ?: "deep_indigo"
        return AVAILABLE_BG_THEMES.find { it.id == themeId } ?: AVAILABLE_BG_THEMES.first()
    }

    fun startLevel(levelNumber: Int) {
        val config = LevelGenerator.getLevel(levelNumber)
        _gameplayState.value = GameplayUiState(
            currentLevelNumber = levelNumber,
            levelConfig = config,
            jars = config.initialJars,
            timeLimitRemainingSeconds = config.timeLimitSeconds
        )
        _currentScreen.value = CurrentScreen.GAMEPLAY

        startLevelTimer()
    }

    private fun startLevelTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                val current = _gameplayState.value
                if (!current.isPaused && !current.isLevelCompleted) {
                    val newElapsed = current.elapsedTimeSeconds + 1
                    val newLimit = if (current.timeLimitRemainingSeconds > 0) {
                        (current.timeLimitRemainingSeconds - 1).coerceAtLeast(0)
                    } else 0

                    _gameplayState.value = current.copy(
                        elapsedTimeSeconds = newElapsed,
                        timeLimitRemainingSeconds = newLimit
                    )

                    // If time limit reached 0 in timed mode
                    if (current.levelConfig?.timeLimitSeconds != null && current.levelConfig.timeLimitSeconds > 0 && newLimit == 0) {
                        // Level time up — restart level
                        restartCurrentLevel()
                    }
                }
            }
        }
    }

    fun pauseGame() {
        _gameplayState.value = _gameplayState.value.copy(isPaused = true)
    }

    fun resumeGame() {
        _gameplayState.value = _gameplayState.value.copy(isPaused = false)
    }

    fun restartCurrentLevel() {
        startLevel(_gameplayState.value.currentLevelNumber)
    }

    fun nextLevel() {
        val nextLvl = _gameplayState.value.currentLevelNumber + 1
        if (nextLvl <= 50) {
            startLevel(nextLvl)
        } else {
            navigateTo(CurrentScreen.LEVEL_SELECT)
        }
    }

    /**
     * Handles tap on a jar.
     */
    fun onJarTapped(jarId: Int) {
        val state = _gameplayState.value
        if (state.isLevelCompleted || state.isPaused || state.isPouring) return

        // If Swap Token mode is active
        if (state.isSwapModeActive) {
            handleSwapTap(jarId)
            return
        }

        val clickedJar = state.jars.firstOrNull { it.id == jarId } ?: return

        // Disallow selecting locked jars directly
        if (clickedJar.isLocked) {
            soundManager.vibrateTap()
            return
        }

        val selectedId = state.selectedJarId

        if (selectedId == null) {
            // Nothing selected yet -> Select this jar if it has liquid
            if (!clickedJar.isEmpty) {
                soundManager.playGlassTap()
                soundManager.vibrateTap()
                _gameplayState.value = state.copy(
                    selectedJarId = jarId,
                    jars = state.jars.map { it.copy(isSelected = (it.id == jarId)) },
                    activeHint = null
                )
            }
        } else if (selectedId == jarId) {
            // Tapped same jar -> Deselect
            soundManager.playGlassTap()
            _gameplayState.value = state.copy(
                selectedJarId = null,
                jars = state.jars.map { it.copy(isSelected = false) }
            )
        } else {
            // Tapped destination jar -> Attempt Pour
            val fromJar = state.jars.firstOrNull { it.id == selectedId } ?: return
            if (JarSolver.canPour(fromJar, clickedJar)) {
                performPour(fromJar, clickedJar)
            } else {
                // Invalid pour: if clicked jar has liquid, switch selection to it
                if (!clickedJar.isEmpty) {
                    soundManager.playGlassTap()
                    soundManager.vibrateTap()
                    _gameplayState.value = state.copy(
                        selectedJarId = jarId,
                        jars = state.jars.map { it.copy(isSelected = (it.id == jarId)) }
                    )
                } else {
                    // Deselect
                    _gameplayState.value = state.copy(
                        selectedJarId = null,
                        jars = state.jars.map { it.copy(isSelected = false) }
                    )
                }
            }
        }
    }

    private fun performPour(fromJar: Jar, toJar: Jar) {
        val state = _gameplayState.value

        val pourCount = fromJar.topConsecutiveCount.coerceAtMost(toJar.availableSpace)
        if (pourCount <= 0) return

        viewModelScope.launch {
            // Start pouring animation
            _gameplayState.value = state.copy(
                isPouring = true,
                pouringFromId = fromJar.id,
                pouringToId = toJar.id,
                selectedJarId = null,
                jars = state.jars.map { it.copy(isSelected = false) }
            )

            soundManager.playPour()
            soundManager.vibrateTap()

            // Smooth pouring delay
            delay(320)

            // Transfer segments
            val movingSegments = fromJar.segments.takeLast(pourCount)
            val newFromSegs = fromJar.segments.dropLast(pourCount).toMutableList()

            var revealedMystery = false
            if (newFromSegs.isNotEmpty() && newFromSegs.last().isHidden) {
                val last = newFromSegs.removeAt(newFromSegs.lastIndex)
                newFromSegs.add(last.copy(isHidden = false))
                revealedMystery = true
            }

            var thawedFrozen = false
            val newToSegs = (toJar.segments + movingSegments).toMutableList()
            // Thaw destination frozen layers if matching color poured on top
            for (i in newToSegs.indices) {
                if (newToSegs[i].isFrozen && newToSegs[i].color == movingSegments.first().color) {
                    newToSegs[i] = newToSegs[i].copy(isFrozen = false)
                    thawedFrozen = true
                }
            }

            // Update rainbow jar counters
            val newToRainbowPours = if (toJar.type == JarType.RAINBOW && toJar.rainbowRemainingPours > 0) {
                toJar.rainbowRemainingPours - 1
            } else toJar.rainbowRemainingPours

            val updatedFrom = fromJar.copy(segments = newFromSegs)
            val updatedTo = toJar.copy(
                segments = newToSegs,
                rainbowRemainingPours = newToRainbowPours
            )

            var updatedJars = state.jars.map {
                when (it.id) {
                    fromJar.id -> updatedFrom
                    toJar.id -> updatedTo
                    else -> it
                }
            }

            // Check if completing this jar unlocks any locked jars
            var unlockedId: Int? = null
            if (updatedTo.isUniformAndFull) {
                soundManager.playJarCompleted()
                soundManager.vibrateSuccess()

                // Unlock any locked jar
                val lockedJar = updatedJars.firstOrNull { it.isLocked }
                if (lockedJar != null) {
                    unlockedId = lockedJar.id
                    updatedJars = updatedJars.map {
                        if (it.id == lockedJar.id) it.copy(isLocked = false) else it
                    }
                }
            }

            // Record move history
            val historyItem = MoveHistory(
                fromJarId = fromJar.id,
                toJarId = toJar.id,
                transferredSegments = movingSegments,
                revealedMysteryLayer = revealedMystery,
                thawedFrozenLayer = thawedFrozen,
                unlockedJarId = unlockedId
            )

            // Calculate combo
            val newCombo = state.comboCount + 1
            val comboBonus = newCombo * 25
            val newScore = state.score + 50 + comboBonus

            if (newCombo > 1) {
                soundManager.playComboChime(newCombo)
            }

            resetComboDecay()

            val isSolved = JarSolver.isGameSolved(updatedJars)

            _gameplayState.value = state.copy(
                jars = updatedJars,
                isPouring = false,
                pouringFromId = null,
                pouringToId = null,
                movesCount = state.movesCount + 1,
                undoHistory = state.undoHistory + historyItem,
                comboCount = newCombo,
                score = newScore
            )

            if (isSolved) {
                handleLevelCompleted()
            }
        }
    }

    private fun resetComboDecay() {
        comboDecayJob?.cancel()
        comboDecayJob = viewModelScope.launch {
            delay(5000) // 5 seconds combo window
            _gameplayState.value = _gameplayState.value.copy(comboCount = 0)
        }
    }

    private fun handleLevelCompleted() {
        val state = _gameplayState.value
        soundManager.playLevelWin()
        soundManager.vibrateCelebration()

        val targetMoves = state.levelConfig?.targetMoves ?: 15
        val actualMoves = state.movesCount + 1
        val stars = when {
            actualMoves <= targetMoves -> 3
            actualMoves <= targetMoves + 4 -> 2
            else -> 1
        }

        val baseCoins = 50
        val starBonus = stars * 20
        val perfectBonus = if (state.isPerfectSort) 50 else 0
        val totalCoinsEarned = baseCoins + starBonus + perfectBonus

        _gameplayState.value = state.copy(
            isLevelCompleted = true,
            starsEarned = stars,
            coinsEarned = totalCoinsEarned,
            showCelebrationParticles = true
        )

        viewModelScope.launch {
            repository.saveLevelCompletion(
                level = state.currentLevelNumber,
                stars = stars,
                moves = actualMoves,
                timeSeconds = state.elapsedTimeSeconds,
                isPerfectSort = state.isPerfectSort,
                coinsEarned = totalCoinsEarned
            )
        }
    }

    /**
     * Undo last move.
     */
    fun undoLastMove() {
        val state = _gameplayState.value
        if (state.undoHistory.isEmpty() || state.isLevelCompleted || state.isPouring) return

        val lastMove = state.undoHistory.last()
        val remainingHistory = state.undoHistory.dropLast(1)

        val fromJar = state.jars.firstOrNull { it.id == lastMove.fromJarId } ?: return
        val toJar = state.jars.firstOrNull { it.id == lastMove.toJarId } ?: return

        val count = lastMove.transferredSegments.size
        val revertedToSegs = toJar.segments.dropLast(count)
        val revertedFromSegs = (fromJar.segments + lastMove.transferredSegments).toMutableList()

        // If mystery layer was revealed during this move, hide it again
        if (lastMove.revealedMysteryLayer && revertedFromSegs.size > count) {
            val mysteryIdx = revertedFromSegs.size - count - 1
            if (mysteryIdx in revertedFromSegs.indices) {
                revertedFromSegs[mysteryIdx] = revertedFromSegs[mysteryIdx].copy(isHidden = true)
            }
        }

        var updatedJars = state.jars.map {
            when (it.id) {
                fromJar.id -> fromJar.copy(segments = revertedFromSegs)
                toJar.id -> toJar.copy(segments = revertedToSegs)
                else -> it
            }
        }

        // Re-lock jar if it was unlocked in this move
        if (lastMove.unlockedJarId != null) {
            updatedJars = updatedJars.map {
                if (it.id == lastMove.unlockedJarId) it.copy(isLocked = true) else it
            }
        }

        soundManager.playGlassTap()
        soundManager.vibrateTap()

        _gameplayState.value = state.copy(
            jars = updatedJars,
            undoHistory = remainingHistory,
            selectedJarId = null,
            isPerfectSort = false // Used undo, so not perfect sort
        )
    }

    /**
     * Use Smart Hint.
     */
    fun useHint() {
        val state = _gameplayState.value
        val stats = gameStats.value ?: return
        if (state.isLevelCompleted || state.isPouring) return

        if (stats.hintsCount > 0) {
            val hintMove = JarSolver.findHint(state.jars)
            if (hintMove != null) {
                soundManager.playPowerUp()
                soundManager.vibrateSuccess()
                _gameplayState.value = state.copy(activeHint = hintMove)

                viewModelScope.launch {
                    repository.updateStats(stats.copy(hintsCount = stats.hintsCount - 1))
                }
            }
        }
    }

    /**
     * Use Extra Tube booster.
     */
    fun useExtraTube() {
        val state = _gameplayState.value
        val stats = gameStats.value ?: return
        if (state.isLevelCompleted || state.isPouring) return

        if (stats.extraTubesCount > 0) {
            val maxId = (state.jars.maxOfOrNull { it.id } ?: 0) + 1
            val newJar = Jar(
                id = maxId,
                capacity = 4,
                segments = emptyList(),
                type = JarType.EXTRA_SLOT
            )
            soundManager.playPowerUp()
            soundManager.vibrateSuccess()

            _gameplayState.value = state.copy(jars = state.jars + newJar)

            viewModelScope.launch {
                repository.updateStats(stats.copy(extraTubesCount = stats.extraTubesCount - 1))
            }
        }
    }

    /**
     * Use Swap Token mode.
     */
    fun useSwapToken() {
        val state = _gameplayState.value
        val stats = gameStats.value ?: return
        if (state.isLevelCompleted || state.isPouring) return

        if (stats.swapTokensCount > 0) {
            soundManager.playPowerUp()
            _gameplayState.value = state.copy(
                isSwapModeActive = true,
                swapFirstJarId = null,
                selectedJarId = null
            )
        }
    }

    private fun handleSwapTap(jarId: Int) {
        val state = _gameplayState.value
        val stats = gameStats.value ?: return

        if (state.swapFirstJarId == null) {
            soundManager.playGlassTap()
            _gameplayState.value = state.copy(
                swapFirstJarId = jarId,
                jars = state.jars.map { it.copy(isSelected = (it.id == jarId)) }
            )
        } else if (state.swapFirstJarId == jarId) {
            // Cancel swap mode
            _gameplayState.value = state.copy(
                isSwapModeActive = false,
                swapFirstJarId = null,
                jars = state.jars.map { it.copy(isSelected = false) }
            )
        } else {
            // Swap jars
            val id1 = state.swapFirstJarId
            val id2 = jarId

            val jar1 = state.jars.firstOrNull { it.id == id1 } ?: return
            val jar2 = state.jars.firstOrNull { it.id == id2 } ?: return

            val updatedJars = state.jars.map {
                when (it.id) {
                    id1 -> jar2.copy(id = id1, isSelected = false)
                    id2 -> jar1.copy(id = id2, isSelected = false)
                    else -> it.copy(isSelected = false)
                }
            }

            soundManager.playPowerUp()
            soundManager.vibrateSuccess()

            _gameplayState.value = state.copy(
                jars = updatedJars,
                isSwapModeActive = false,
                swapFirstJarId = null
            )

            viewModelScope.launch {
                repository.updateStats(stats.copy(swapTokensCount = stats.swapTokensCount - 1))
            }
        }
    }

    /**
     * Use Time Crystal power-up.
     */
    fun useTimeCrystal() {
        val state = _gameplayState.value
        val stats = gameStats.value ?: return
        if (state.isLevelCompleted || state.isPouring) return

        if (stats.timeCrystalsCount > 0) {
            soundManager.playPowerUp()
            soundManager.vibrateSuccess()

            val newLimit = if (state.timeLimitRemainingSeconds > 0) state.timeLimitRemainingSeconds + 20 else 0
            _gameplayState.value = state.copy(
                timeLimitRemainingSeconds = newLimit,
                comboCount = maxOf(state.comboCount, 2)
            )

            viewModelScope.launch {
                repository.updateStats(stats.copy(timeCrystalsCount = stats.timeCrystalsCount - 1))
            }
        }
    }

    /**
     * Toggle sound/vibration/colorblind settings.
     */
    fun toggleSound() {
        val stats = gameStats.value ?: return
        val newSound = !stats.soundEnabled
        soundManager.isSoundEnabled = newSound
        viewModelScope.launch {
            repository.updateStats(stats.copy(soundEnabled = newSound))
        }
    }

    fun toggleVibration() {
        val stats = gameStats.value ?: return
        val newVibe = !stats.vibrationEnabled
        soundManager.isVibrationEnabled = newVibe
        viewModelScope.launch {
            repository.updateStats(stats.copy(vibrationEnabled = newVibe))
        }
    }

    fun toggleColorblindMode() {
        val stats = gameStats.value ?: return
        val newMode = !stats.colorblindMode
        viewModelScope.launch {
            repository.updateStats(stats.copy(colorblindMode = newMode))
        }
    }

    fun unlockOrSelectSkin(skin: JarSkinTheme) {
        val stats = gameStats.value ?: return
        val unlockedList = stats.unlockedJarSkins.split(",")
        if (unlockedList.contains(skin.id)) {
            viewModelScope.launch {
                repository.updateStats(stats.copy(selectedJarSkinId = skin.id))
            }
        } else {
            viewModelScope.launch {
                repository.unlockSkin(skin.id, skin.costCoins)
            }
        }
    }

    fun unlockOrSelectTheme(theme: BackgroundTheme) {
        val stats = gameStats.value ?: return
        val unlockedList = stats.unlockedBgThemes.split(",")
        if (unlockedList.contains(theme.id)) {
            viewModelScope.launch {
                repository.updateStats(stats.copy(selectedBgThemeId = theme.id))
            }
        } else {
            viewModelScope.launch {
                repository.unlockTheme(theme.id, theme.costCoins)
            }
        }
    }

    fun buyPowerUp(type: PowerUpType) {
        viewModelScope.launch {
            repository.buyPowerUp(type)
        }
    }

    fun claimRewardedAdBonus() {
        val stats = gameStats.value ?: return
        soundManager.playPowerUp()
        soundManager.vibrateSuccess()
        viewModelScope.launch {
            repository.updateStats(
                stats.copy(
                    totalCoins = stats.totalCoins + 100,
                    hintsCount = stats.hintsCount + 1,
                    extraTubesCount = stats.extraTubesCount + 1
                )
            )
        }
    }

    fun removeAds() {
        val stats = gameStats.value ?: return
        soundManager.playPowerUp()
        viewModelScope.launch {
            repository.updateStats(stats.copy(hasRemovedAds = true))
        }
    }
}
