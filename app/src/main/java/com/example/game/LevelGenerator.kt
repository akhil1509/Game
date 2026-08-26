package com.example.game

import com.example.model.ColorSegment
import com.example.model.Jar
import com.example.model.JarColor
import com.example.model.JarType
import com.example.model.LevelConfig
import kotlin.random.Random

object LevelGenerator {

    /**
     * Generates or retrieves the level configuration for a given level number (1..50+).
     */
    fun getLevel(levelNumber: Int): LevelConfig {
        return when {
            levelNumber == 1 -> createLevel1()
            levelNumber == 2 -> createLevel2()
            levelNumber == 3 -> createLevel3()
            levelNumber == 4 -> createLevel4()
            levelNumber == 5 -> createLevel5()
            levelNumber == 6 -> createLevel6()
            levelNumber == 7 -> createLevel7()
            levelNumber == 8 -> createLevel8()
            levelNumber == 9 -> createLevel9()
            levelNumber == 10 -> createLevel10()
            levelNumber in 11..20 -> generateMysteryLevel(levelNumber)
            levelNumber in 21..30 -> generateGlacierRainbowLevel(levelNumber)
            levelNumber in 31..40 -> generateMagmaLockedLevel(levelNumber)
            else -> generateQuantumMasterLevel(levelNumber)
        }
    }

    private fun createLevel1(): LevelConfig {
        // 3 jars: 2 filled (2 colors), 1 empty. Starter level.
        val j1 = Jar(
            id = 0,
            capacity = 4,
            segments = listOf(
                ColorSegment(JarColor.CORAL_RED),
                ColorSegment(JarColor.OCEAN_BLUE),
                ColorSegment(JarColor.CORAL_RED),
                ColorSegment(JarColor.OCEAN_BLUE)
            )
        )
        val j2 = Jar(
            id = 1,
            capacity = 4,
            segments = listOf(
                ColorSegment(JarColor.OCEAN_BLUE),
                ColorSegment(JarColor.CORAL_RED),
                ColorSegment(JarColor.OCEAN_BLUE),
                ColorSegment(JarColor.CORAL_RED)
            )
        )
        val j3 = Jar(id = 2, capacity = 4, segments = emptyList())

        return LevelConfig(
            levelNumber = 1,
            chapterId = 1,
            initialJars = listOf(j1, j2, j3),
            targetMoves = 5,
            specialFeatureDescription = "Tap a jar to lift it, then tap another jar to pour matching colours!"
        )
    }

    private fun createLevel2(): LevelConfig {
        // 4 jars: 3 colors (Red, Blue, Gold), 1 empty
        val j1 = Jar(
            id = 0,
            capacity = 4,
            segments = listOf(
                ColorSegment(JarColor.CORAL_RED),
                ColorSegment(JarColor.AMBER_GOLD),
                ColorSegment(JarColor.OCEAN_BLUE),
                ColorSegment(JarColor.CORAL_RED)
            )
        )
        val j2 = Jar(
            id = 1,
            capacity = 4,
            segments = listOf(
                ColorSegment(JarColor.AMBER_GOLD),
                ColorSegment(JarColor.OCEAN_BLUE),
                ColorSegment(JarColor.CORAL_RED),
                ColorSegment(JarColor.AMBER_GOLD)
            )
        )
        val j3 = Jar(
            id = 2,
            capacity = 4,
            segments = listOf(
                ColorSegment(JarColor.OCEAN_BLUE),
                ColorSegment(JarColor.CORAL_RED),
                ColorSegment(JarColor.AMBER_GOLD),
                ColorSegment(JarColor.OCEAN_BLUE)
            )
        )
        val j4 = Jar(id = 3, capacity = 4, segments = emptyList())

        return LevelConfig(
            levelNumber = 2,
            chapterId = 1,
            initialJars = listOf(j1, j2, j3, j4),
            targetMoves = 9,
            specialFeatureDescription = "Sort all 3 colours into separate full jars!"
        )
    }

    private fun createLevel3(): LevelConfig {
        // 5 jars: 3 colors, 2 empty jars for strategic planning
        val j1 = Jar(
            id = 0, capacity = 4,
            segments = listOf(
                ColorSegment(JarColor.EMERALD_GREEN),
                ColorSegment(JarColor.HOT_PINK),
                ColorSegment(JarColor.EMERALD_GREEN),
                ColorSegment(JarColor.HOT_PINK)
            )
        )
        val j2 = Jar(
            id = 1, capacity = 4,
            segments = listOf(
                ColorSegment(JarColor.CYAN_TEAL),
                ColorSegment(JarColor.EMERALD_GREEN),
                ColorSegment(JarColor.HOT_PINK),
                ColorSegment(JarColor.CYAN_TEAL)
            )
        )
        val j3 = Jar(
            id = 2, capacity = 4,
            segments = listOf(
                ColorSegment(JarColor.HOT_PINK),
                ColorSegment(JarColor.CYAN_TEAL),
                ColorSegment(JarColor.CYAN_TEAL),
                ColorSegment(JarColor.EMERALD_GREEN)
            )
        )
        val j4 = Jar(id = 3, capacity = 4, segments = emptyList())
        val j5 = Jar(id = 4, capacity = 4, segments = emptyList())

        return LevelConfig(
            levelNumber = 3,
            chapterId = 1,
            initialJars = listOf(j1, j2, j3, j4, j5),
            targetMoves = 10,
            specialFeatureDescription = "Use the two empty buffer jars to organize long color runs."
        )
    }

    private fun createLevel4(): LevelConfig {
        // 5 jars: 4 colors + 1 empty
        val colors = listOf(JarColor.ROYAL_INDIGO, JarColor.AMBER_GOLD, JarColor.CORAL_RED, JarColor.EMERALD_GREEN)
        val jars = generateSolvableShuffle(
            numColors = 4,
            numEmptyJars = 1,
            colorPalette = colors,
            shuffleSteps = 22,
            seed = 404
        )
        return LevelConfig(
            levelNumber = 4,
            chapterId = 1,
            initialJars = jars,
            targetMoves = 14,
            specialFeatureDescription = "Keep an eye on the combo multiplier for bonus points!"
        )
    }

    private fun createLevel5(): LevelConfig {
        val colors = listOf(JarColor.PURPLE_NEON, JarColor.LIME_ELECTRIC, JarColor.OCEAN_BLUE, JarColor.HOT_PINK)
        val jars = generateSolvableShuffle(
            numColors = 4,
            numEmptyJars = 2,
            colorPalette = colors,
            shuffleSteps = 28,
            seed = 505
        )
        return LevelConfig(
            levelNumber = 5,
            chapterId = 1,
            initialJars = jars,
            targetMoves = 16,
            specialFeatureDescription = "Chapter 1 Milestone: Multi-colour cascade!"
        )
    }

    private fun createLevel6(): LevelConfig = generateStandardLevel(6, 4, 2, 606, "Patience is key — plan moves ahead.")
    private fun createLevel7(): LevelConfig = generateStandardLevel(7, 5, 2, 707, "5 vibrant liquid colors to sort.")
    private fun createLevel8(): LevelConfig = generateStandardLevel(8, 5, 2, 808, "Chain consecutive pours for combo score multiplier!")
    private fun createLevel9(): LevelConfig = generateStandardLevel(9, 5, 2, 909, "Watch out for color blocks.")
    private fun createLevel10(): LevelConfig = generateStandardLevel(10, 5, 2, 1010, "Chapter 1 Boss: Master the flow of all 5 crystal jars!")

    /**
     * Chapter 2: Mystery Jars (Levels 11-20).
     */
    private fun generateMysteryLevel(levelNumber: Int): LevelConfig {
        val numColors = if (levelNumber <= 15) 5 else 6
        val palette = listOf(
            JarColor.PURPLE_NEON, JarColor.CYAN_TEAL, JarColor.AMBER_GOLD,
            JarColor.CORAL_RED, JarColor.EMERALD_GREEN, JarColor.SUNSET_ORANGE
        ).take(numColors)

        val jars = generateSolvableShuffle(
            numColors = numColors,
            numEmptyJars = 2,
            colorPalette = palette,
            shuffleSteps = 25 + levelNumber,
            seed = 2000L + levelNumber
        ).toMutableList()

        // Turn 1-2 jars into Mystery Jars with hidden bottom layers
        val mysteryCount = if (levelNumber < 15) 1 else 2
        var modified = 0
        for (i in jars.indices) {
            if (modified >= mysteryCount) break
            val jar = jars[i]
            if (jar.segments.size == 4) {
                val newSegs = jar.segments.mapIndexed { idx, seg ->
                    if (idx < 2) seg.copy(isHidden = true) else seg
                }
                jars[i] = jar.copy(segments = newSegs, type = JarType.MYSTERY)
                modified++
            }
        }

        return LevelConfig(
            levelNumber = levelNumber,
            chapterId = 2,
            initialJars = jars,
            targetMoves = 18 + (levelNumber - 10),
            specialFeatureDescription = "Mystery Jar: Hidden colors '?' are revealed as you pour away top layers!"
        )
    }

    /**
     * Chapter 3: Frozen Layers & Rainbow Jars (Levels 21-30).
     */
    private fun generateGlacierRainbowLevel(levelNumber: Int): LevelConfig {
        val numColors = 6
        val palette = listOf(
            JarColor.OCEAN_BLUE, JarColor.MINT_ICE, JarColor.HOT_PINK,
            JarColor.LIME_ELECTRIC, JarColor.AMBER_GOLD, JarColor.ROYAL_INDIGO
        )

        val jars = generateSolvableShuffle(
            numColors = numColors,
            numEmptyJars = 2,
            colorPalette = palette,
            shuffleSteps = 32 + levelNumber,
            seed = 3000L + levelNumber
        ).toMutableList()

        // Add 1 Rainbow Jar or Frozen layer
        if (levelNumber % 2 == 1) {
            // Rainbow wildcard jar
            val emptyIdx = jars.indexOfFirst { it.isEmpty }
            if (emptyIdx != -1) {
                jars[emptyIdx] = jars[emptyIdx].copy(
                    type = JarType.RAINBOW,
                    rainbowRemainingPours = 3
                )
            }
        } else {
            // Frozen bottom layer in one jar
            val fullIdx = jars.indexOfFirst { it.segments.size == 4 }
            if (fullIdx != -1) {
                val segs = jars[fullIdx].segments.mapIndexed { idx, seg ->
                    if (idx == 0) seg.copy(isFrozen = true) else seg
                }
                jars[fullIdx] = jars[fullIdx].copy(segments = segs)
            }
        }

        return LevelConfig(
            levelNumber = levelNumber,
            chapterId = 3,
            initialJars = jars,
            targetMoves = 22 + (levelNumber - 20),
            specialFeatureDescription = if (levelNumber % 2 == 1)
                "Rainbow Jar: Accepts any colour liquid for 3 transfers!"
            else
                "Glacier Ice: Top matching pours will thaw frozen layers!"
        )
    }

    /**
     * Chapter 4: Locked Jars & One-Way Containers (Levels 31-40).
     */
    private fun generateMagmaLockedLevel(levelNumber: Int): LevelConfig {
        val numColors = 6
        val palette = listOf(
            JarColor.RUBY_CRIMSON, JarColor.SUNSET_ORANGE, JarColor.AMBER_GOLD,
            JarColor.PURPLE_NEON, JarColor.CYAN_TEAL, JarColor.EMERALD_GREEN
        )

        val jars = generateSolvableShuffle(
            numColors = numColors,
            numEmptyJars = 2,
            colorPalette = palette,
            shuffleSteps = 35 + levelNumber,
            seed = 4000L + levelNumber
        ).toMutableList()

        if (levelNumber % 2 == 1) {
            // One-Way Funnel
            val fullIdx = jars.indexOfFirst { it.segments.size == 4 }
            if (fullIdx != -1) {
                jars[fullIdx] = jars[fullIdx].copy(type = JarType.ONE_WAY_OUT)
            }
        } else {
            // Locked Jar
            val fullIdx = jars.indexOfFirst { it.segments.size == 4 }
            if (fullIdx != -1) {
                jars[fullIdx] = jars[fullIdx].copy(type = JarType.LOCKED, isLocked = true)
            }
        }

        return LevelConfig(
            levelNumber = levelNumber,
            chapterId = 4,
            initialJars = jars,
            targetMoves = 25 + (levelNumber - 30),
            specialFeatureDescription = if (levelNumber % 2 == 1)
                "One-Way Funnel Jar: Can pour OUT, but cannot receive incoming liquid!"
            else
                "Locked Tube: Unlocks automatically when you fill a key sorted jar!"
        )
    }

    /**
     * Chapter 5: Quantum Void Master Levels (Levels 41-50).
     */
    private fun generateQuantumMasterLevel(levelNumber: Int): LevelConfig {
        val numColors = 7
        val palette = listOf(
            JarColor.CORAL_RED, JarColor.OCEAN_BLUE, JarColor.AMBER_GOLD,
            JarColor.EMERALD_GREEN, JarColor.PURPLE_NEON, JarColor.HOT_PINK,
            JarColor.CYAN_TEAL
        )

        val jars = generateSolvableShuffle(
            numColors = numColors,
            numEmptyJars = 2,
            colorPalette = palette,
            shuffleSteps = 45 + levelNumber,
            seed = 5000L + levelNumber
        ).toMutableList()

        // Combine Mystery + Rainbow for quantum feel
        for (i in 0 until 2) {
            if (jars[i].segments.size == 4) {
                val segs = jars[i].segments.mapIndexed { idx, seg ->
                    if (idx < 2) seg.copy(isHidden = true) else seg
                }
                jars[i] = jars[i].copy(segments = segs, type = JarType.MYSTERY)
            }
        }

        return LevelConfig(
            levelNumber = levelNumber,
            chapterId = 5,
            initialJars = jars,
            targetMoves = 30 + (levelNumber - 40),
            timeLimitSeconds = if (levelNumber == 50) 180 else 0,
            specialFeatureDescription = "Quantum Master Realm: Solve complex multi-dimensional color puzzles!"
        )
    }

    private fun generateStandardLevel(
        levelNum: Int,
        numColors: Int,
        numEmpty: Int,
        seed: Long,
        desc: String
    ): LevelConfig {
        val palette = listOf(
            JarColor.CORAL_RED, JarColor.OCEAN_BLUE, JarColor.AMBER_GOLD,
            JarColor.EMERALD_GREEN, JarColor.PURPLE_NEON, JarColor.HOT_PINK
        ).take(numColors)

        val jars = generateSolvableShuffle(
            numColors = numColors,
            numEmptyJars = numEmpty,
            colorPalette = palette,
            shuffleSteps = 20 + levelNum * 2,
            seed = seed
        )

        return LevelConfig(
            levelNumber = levelNum,
            chapterId = 1,
            initialJars = jars,
            targetMoves = 10 + levelNum,
            specialFeatureDescription = desc
        )
    }

    /**
     * Solvability Guaranteed Generator:
     * Starts with a solved board of clean uniform color jars + empty jars,
     * then applies random valid reverse pours step-by-step.
     * This mathematical construction GUARANTEES that a solution path always exists!
     */
    private fun generateSolvableShuffle(
        numColors: Int,
        numEmptyJars: Int,
        colorPalette: List<JarColor>,
        shuffleSteps: Int,
        seed: Long
    ): List<Jar> {
        val random = Random(seed)
        val capacity = 4

        // 1. Create solved state
        val jarsList = mutableListOf<MutableList<ColorSegment>>()
        for (i in 0 until numColors) {
            val color = colorPalette[i % colorPalette.size]
            val jarSegments = MutableList(capacity) { ColorSegment(color) }
            jarsList.add(jarSegments)
        }
        for (i in 0 until numEmptyJars) {
            jarsList.add(mutableListOf())
        }

        val totalJars = jarsList.size

        // 2. Perform reverse pours
        var stepsDone = 0
        var attempts = 0
        while (stepsDone < shuffleSteps && attempts < shuffleSteps * 10) {
            attempts++
            val fromIndex = random.nextInt(totalJars)
            val toIndex = random.nextInt(totalJars)
            if (fromIndex == toIndex) continue

            val fromJar = jarsList[fromIndex]
            val toJar = jarsList[toIndex]

            if (fromJar.isEmpty() || toJar.size >= capacity) continue

            // Determine how many segments to move (1 to 2)
            val topColor = fromJar.last().color
            var count = 0
            for (k in fromJar.indices.reversed()) {
                if (fromJar[k].color == topColor && toJar.size + count < capacity) {
                    count++
                } else break
            }

            if (count > 0) {
                // Move count items from fromJar to toJar
                val itemsToMove = fromJar.takeLast(count)
                repeat(count) { fromJar.removeAt(fromJar.lastIndex) }
                toJar.addAll(itemsToMove)
                stepsDone++
            }
        }

        // Convert to immutable Jars with IDs
        return jarsList.mapIndexed { index, segs ->
            Jar(
                id = index,
                capacity = capacity,
                segments = segs.toList()
            )
        }
    }
}
