package com.example.model

import androidx.compose.ui.graphics.Color

/**
 * Color definitions with vibrant contrasts, glow effects, and colorblind glyph symbols.
 */
enum class JarColor(
    val id: Int,
    val displayName: String,
    val primaryColor: Color,
    val darkColor: Color,
    val lightColor: Color,
    val symbol: String // Colorblind accessibility pattern/symbol
) {
    CORAL_RED(1, "Coral Red", Color(0xFFFF3366), Color(0xFFC2185B), Color(0xFFFF80AB), "▲"),
    OCEAN_BLUE(2, "Ocean Blue", Color(0xFF00D2FF), Color(0xFF0072FF), Color(0xFF80E8FF), "●"),
    AMBER_GOLD(3, "Amber Gold", Color(0xFFFFB300), Color(0xFFFF8F00), Color(0xFFFFE082), "★"),
    EMERALD_GREEN(4, "Emerald Green", Color(0xFF00E676), Color(0xFF00A854), Color(0xFF80FFBB), "◆"),
    PURPLE_NEON(5, "Purple Neon", Color(0xFFB388FF), Color(0xFF7C4DFF), Color(0xFFD1B3FF), "✚"),
    HOT_PINK(6, "Hot Pink", Color(0xFFFF4081), Color(0xFFC51162), Color(0xFFFF80AB), "♥"),
    LIME_ELECTRIC(7, "Lime Electric", Color(0xFFAEEA00), Color(0xFF76A000), Color(0xFFCCFF90), "■"),
    CYAN_TEAL(8, "Cyan Teal", Color(0xFF00F5D4), Color(0xFF00BFA5), Color(0xFF80FFF0), "✦"),
    SUNSET_ORANGE(9, "Sunset Orange", Color(0xFFFF6D00), Color(0xFFE65100), Color(0xFFFFAB40), "▼"),
    ROYAL_INDIGO(10, "Royal Indigo", Color(0xFF536DFE), Color(0xFF304FFE), Color(0xFF8C9EFF), "⬟"),
    RUBY_CRIMSON(11, "Ruby Crimson", Color(0xFFD50000), Color(0xFF8B0000), Color(0xFFFF5252), "⬢"),
    MINT_ICE(12, "Mint Ice", Color(0xFF64FFDA), Color(0xFF1DE9B6), Color(0xFFA7FFEB), "❄");

    companion object {
        fun fromId(id: Int): JarColor = entries.firstOrNull { it.id == id } ?: OCEAN_BLUE
    }
}

/**
 * Individual layer inside a jar.
 */
data class ColorSegment(
    val color: JarColor,
    val isHidden: Boolean = false,    // Mystery jar layer: hidden until top layers cleared
    val isFrozen: Boolean = false     // Frozen layer: must thaw before pouring
)

/**
 * Type of Jar / Tube with special mechanics.
 */
enum class JarType {
    STANDARD,      // Regular glass tube (capacity 4)
    MYSTERY,       // Has hidden '?' layers underneath
    RAINBOW,       // Wildcard tube that can accept any colour temporarily
    LOCKED,        // Locked tube with padlock until unlocked
    ONE_WAY_OUT,   // Funnel jar: can only pour out, cannot receive
    ONE_WAY_IN,    // Funnel jar: can only receive, cannot pour out
    EXTRA_SLOT     // Temporary booster tube added during gameplay
}

/**
 * Represents a single Jar on the playfield.
 */
data class Jar(
    val id: Int,
    val capacity: Int = 4,
    val segments: List<ColorSegment> = emptyList(),
    val type: JarType = JarType.STANDARD,
    val isLocked: Boolean = false,
    val isCompleted: Boolean = false,
    val isSelected: Boolean = false,
    val rainbowRemainingPours: Int = 0 // If RAINBOW type, remaining flexible pours
) {
    val size: Int get() = segments.size
    val isFull: Boolean get() = segments.size >= capacity
    val isEmpty: Boolean get() = segments.isEmpty()
    val availableSpace: Int get() = (capacity - segments.size).coerceAtLeast(0)

    val topSegment: ColorSegment? get() = segments.lastOrNull()
    val topColor: JarColor? get() = topSegment?.color

    /**
     * Number of consecutive identical visible top segments.
     */
    val topConsecutiveCount: Int
        get() {
            if (segments.isEmpty()) return 0
            val top = segments.last()
            if (top.isHidden || top.isFrozen) return 0
            var count = 0
            for (i in segments.indices.reversed()) {
                if (segments[i].color == top.color && !segments[i].isHidden && !segments[i].isFrozen) {
                    count++
                } else {
                    break
                }
            }
            return count
        }

    /**
     * A jar is sorted if it is full and all segments are the same color,
     * or if it's empty (or not needed).
     */
    val isUniformAndFull: Boolean
        get() {
            if (segments.size != capacity) return false
            val first = segments.first().color
            return segments.all { it.color == first && !it.isHidden && !it.isFrozen }
        }
}
