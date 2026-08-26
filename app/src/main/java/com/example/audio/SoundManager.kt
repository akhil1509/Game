package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * Procedural Audio Synthesizer and Haptics Manager.
 * Produces crisp glass chimes, liquid pouring bubbles, combo chords, and tactile vibrations.
 */
class SoundManager(private val context: Context) {

    private val audioScope = CoroutineScope(Dispatchers.Default)
    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    var isSoundEnabled: Boolean = true
    var isVibrationEnabled: Boolean = true

    private val sampleRate = 22050

    /**
     * Plays a gentle glass tap sound when selecting a jar.
     */
    fun playGlassTap() {
        if (!isSoundEnabled) return
        audioScope.launch {
            val durationMs = 120
            val numSamples = (sampleRate * durationMs / 1000)
            val buffer = ShortArray(numSamples)

            val freq1 = 1200.0 // Glass ping fundamental
            val freq2 = 2400.0 // Harmonic
            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val env = exp(-t * 28.0) // Quick decay
                val sample = (sin(2 * PI * freq1 * t) * 0.7 + sin(2 * PI * freq2 * t) * 0.3) * env
                buffer[i] = (sample * Short.MAX_VALUE * 0.55).toInt().toShort()
            }
            playPcm(buffer)
        }
    }

    /**
     * Plays a pleasant liquid pouring sound with bubbling frequency modulations.
     */
    fun playPour() {
        if (!isSoundEnabled) return
        audioScope.launch {
            val durationMs = 320
            val numSamples = (sampleRate * durationMs / 1000)
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                // Frequency bubble modulation
                val modFreq = 480.0 + 160.0 * sin(2 * PI * 18.0 * t) + 80.0 * sin(2 * PI * 34.0 * t)
                val env = sin(PI * (i.toDouble() / numSamples)) // Soft attack and release
                val sample = sin(2 * PI * modFreq * t) * env
                buffer[i] = (sample * Short.MAX_VALUE * 0.45).toInt().toShort()
            }
            playPcm(buffer)
        }
    }

    /**
     * Plays a sparkling combo chime based on combo level (ascending musical notes).
     */
    fun playComboChime(comboIndex: Int) {
        if (!isSoundEnabled) return
        audioScope.launch {
            // Pentatonic scale frequencies
            val scale = listOf(523.25, 587.33, 659.25, 783.99, 880.0, 1046.50, 1174.66)
            val baseFreq = scale[(comboIndex - 1).coerceIn(0, scale.lastIndex)]

            val durationMs = 280
            val numSamples = (sampleRate * durationMs / 1000)
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val env = exp(-t * 12.0)
                val sample = (sin(2 * PI * baseFreq * t) * 0.6 + sin(2 * PI * (baseFreq * 2) * t) * 0.4) * env
                buffer[i] = (sample * Short.MAX_VALUE * 0.65).toInt().toShort()
            }
            playPcm(buffer)
        }
    }

    /**
     * Jar completed sort fanfare (satisfying harmonic chime).
     */
    fun playJarCompleted() {
        if (!isSoundEnabled) return
        audioScope.launch {
            val durationMs = 450
            val numSamples = (sampleRate * durationMs / 1000)
            val buffer = ShortArray(numSamples)

            // Major triad chord: C6, E6, G6
            val f1 = 1046.50
            val f2 = 1318.51
            val f3 = 1567.98

            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val env = exp(-t * 8.0)
                val sample = (sin(2 * PI * f1 * t) * 0.4 + sin(2 * PI * f2 * t) * 0.35 + sin(2 * PI * f3 * t) * 0.25) * env
                buffer[i] = (sample * Short.MAX_VALUE * 0.7).toInt().toShort()
            }
            playPcm(buffer)
        }
    }

    /**
     * Level victory celebration fanfare.
     */
    fun playLevelWin() {
        if (!isSoundEnabled) return
        audioScope.launch {
            val durationMs = 700
            val numSamples = (sampleRate * durationMs / 1000)
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val env = exp(-t * 5.0)
                val sweepFreq = 650.0 + (t * 800.0) // Ascending shimmer
                val sample = (sin(2 * PI * sweepFreq * t) * 0.5 + sin(2 * PI * 1318.51 * t) * 0.5) * env
                buffer[i] = (sample * Short.MAX_VALUE * 0.75).toInt().toShort()
            }
            playPcm(buffer)
        }
    }

    /**
     * PowerUp activation whoosh sound.
     */
    fun playPowerUp() {
        if (!isSoundEnabled) return
        audioScope.launch {
            val durationMs = 300
            val numSamples = (sampleRate * durationMs / 1000)
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val env = sin(PI * (i.toDouble() / numSamples))
                val freq = 400.0 + (t * 1200.0)
                val sample = sin(2 * PI * freq * t) * env
                buffer[i] = (sample * Short.MAX_VALUE * 0.6).toInt().toShort()
            }
            playPcm(buffer)
        }
    }

    /**
     * Plays tactile haptic feedback.
     */
    fun vibrateTap() {
        if (!isVibrationEnabled || vibrator == null) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(18)
            }
        } catch (_: Exception) {}
    }

    fun vibrateSuccess() {
        if (!isVibrationEnabled || vibrator == null) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(45)
            }
        } catch (_: Exception) {}
    }

    fun vibrateCelebration() {
        if (!isVibrationEnabled || vibrator == null) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 30, 40, 50, 40, 80), -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(150)
            }
        } catch (_: Exception) {}
    }

    private fun playPcm(buffer: ShortArray) {
        try {
            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(buffer, 0, buffer.size)
            audioTrack.play()
            // Release after playing
            audioScope.launch {
                kotlinx.coroutines.delay((buffer.size * 1000L / sampleRate) + 50)
                try {
                    audioTrack.stop()
                    audioTrack.release()
                } catch (_: Exception) {}
            }
        } catch (_: Exception) {}
    }
}
