package com.example.model

/**
 * Chapter / Theme World grouping.
 */
data class Chapter(
    val id: Int,
    val title: String,
    val description: String,
    val iconName: String,
    val requiredStars: Int,
    val levelRange: IntRange
)

val GAME_CHAPTERS = listOf(
    Chapter(
        id = 1,
        title = "Crystal Spring",
        description = "Gentle introduction to liquid flow and basic colors",
        iconName = "water_drop",
        requiredStars = 0,
        levelRange = 1..10
    ),
    Chapter(
        id = 2,
        title = "Neon Oasis",
        description = "Mystery jars & multi-color spectrums",
        iconName = "auto_awesome",
        requiredStars = 20,
        levelRange = 11..20
    ),
    Chapter(
        id = 3,
        title = "Glacier Peaks",
        description = "Frozen color layers & rainbow wildcard jars",
        iconName = "ac_unit",
        requiredStars = 45,
        levelRange = 21..30
    ),
    Chapter(
        id = 4,
        title = "Magma Chamber",
        description = "Locked tubes & one-way funnels",
        iconName = "local_fire_department",
        requiredStars = 70,
        levelRange = 31..40
    ),
    Chapter(
        id = 5,
        title = "Quantum Void",
        description = "Master-tier complex arrangements & time crystals",
        iconName = "psychology",
        requiredStars = 95,
        levelRange = 41..50
    )
)

/**
 * Level definition.
 */
data class LevelConfig(
    val levelNumber: Int,
    val chapterId: Int,
    val initialJars: List<Jar>,
    val targetMoves: Int,
    val timeLimitSeconds: Int = 0, // 0 = unlimited chill mode
    val specialFeatureDescription: String? = null
)

/**
 * Move history item for Undo functionality.
 */
data class MoveHistory(
    val fromJarId: Int,
    val toJarId: Int,
    val transferredSegments: List<ColorSegment>,
    val revealedMysteryLayer: Boolean = false,
    val thawedFrozenLayer: Boolean = false,
    val unlockedJarId: Int? = null
)

/**
 * PowerUp Types.
 */
enum class PowerUpType(
    val displayName: String,
    val description: String,
    val coinCost: Int,
    val icon: String
) {
    EXTRA_TUBE("Extra Tube", "Adds an extra empty glass jar to ease transfers", 150, "➕"),
    HINT("Smart Hint", "Reveals the optimal next move", 50, "💡"),
    SWAP_TOKEN("Swap Jars", "Swap positions of any two jars", 100, "🔄"),
    TIME_CRYSTAL("Time Freeze", "Freezes combo decay & grants 15s extra time", 75, "⏳"),
    RAINBOW_SPLASH("Rainbow Jar", "Converts a jar to accept any incoming color", 120, "🌈")
}

/**
 * Visual Cosmetic Themes for Jars and Backgrounds.
 */
data class JarSkinTheme(
    val id: String,
    val name: String,
    val description: String,
    val isPremium: Boolean,
    val costCoins: Int,
    val glassBorderColorHex: Long,
    val glassGlowHex: Long,
    val neckRadiusDp: Float = 16f,
    val bottomRadiusDp: Float = 22f
)

data class BackgroundTheme(
    val id: String,
    val name: String,
    val isPremium: Boolean,
    val costCoins: Int,
    val topColorHex: Long,
    val bottomColorHex: Long,
    val accentColorHex: Long
)

val AVAILABLE_JAR_SKINS = listOf(
    JarSkinTheme("classic", "Classic Glass", "Standard laboratory crystal glassware", false, 0, 0xAAFFFFFF, 0x33FFFFFF, 16f, 22f),
    JarSkinTheme("neon_glow", "Neon Prism", "Vibrant edge-illuminated neon cylinder", true, 300, 0xFF00E5FF, 0x6600E5FF, 8f, 14f),
    JarSkinTheme("potion_flask", "Alchemist Flask", "Spherical potion flask with golden rim", true, 600, 0xFFFFD700, 0x44FFD700, 24f, 30f),
    JarSkinTheme("cyber_capsule", "Cyber Capsule", "Futuristic sci-fi containment tube", true, 1000, 0xFFB388FF, 0x55B388FF, 12f, 18f)
)

val AVAILABLE_BG_THEMES = listOf(
    BackgroundTheme("deep_indigo", "Midnight Nebula", false, 0, 0xFF0F172A, 0xFF1E1B4B, 0xFF6366F1),
    BackgroundTheme("sunset_bliss", "Sunset Horizon", true, 250, 0xFF1A0B2E, 0xFF311042, 0xFFFF6D00),
    BackgroundTheme("emerald_lounge", "Emerald Forest", true, 400, 0xFF06281E, 0xFF0B3D2E, 0xFF00E676),
    BackgroundTheme("cyber_dark", "Cyber Matrix", true, 800, 0xFF0A0E17, 0xFF111827, 0xFF00F5D4)
)
