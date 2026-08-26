package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class Particle(
    val originX: Float,
    val originY: Float,
    val angle: Double,
    val speed: Float,
    val color: Color,
    val size: Float,
    val rotationSpeed: Float,
    val shapeType: Int // 0: star, 1: circle, 2: rectangle
)

@Composable
fun CelebrationParticleEffect(
    modifier: Modifier = Modifier,
    particleCount: Int = 70,
    burstColors: List<Color> = listOf(
        Color(0xFFFF3366), Color(0xFF00D2FF), Color(0xFFFFB300),
        Color(0xFF00E676), Color(0xFFB388FF), Color(0xFFFFD700)
    )
) {
    val progress = remember { Animatable(0f) }

    val particles = remember {
        val random = Random(System.currentTimeMillis())
        List(particleCount) {
            val angle = random.nextDouble(0.0, 2 * Math.PI)
            val speed = random.nextFloat() * 450f + 150f
            val color = burstColors[random.nextInt(burstColors.size)]
            val size = random.nextFloat() * 10f + 6f
            val rotSpeed = (random.nextFloat() - 0.5f) * 720f
            val shape = random.nextInt(3)
            Particle(
                originX = 0.5f,
                originY = 0.45f,
                angle = angle,
                speed = speed,
                color = color,
                size = size,
                rotationSpeed = rotSpeed,
                shapeType = shape
            )
        }
    }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1400, easing = LinearEasing)
        )
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val p = progress.value
        val alpha = (1f - p * 0.9f).coerceIn(0f, 1f)

        particles.forEach { pt ->
            val distance = pt.speed * p
            // Gravity effect
            val gravity = 350f * p * p
            val currentX = (size.width * pt.originX) + (cos(pt.angle) * distance).toFloat()
            val currentY = (size.height * pt.originY) + (sin(pt.angle) * distance).toFloat() + gravity

            val currentRotation = pt.rotationSpeed * p

            rotate(degrees = currentRotation, pivot = Offset(currentX, currentY)) {
                when (pt.shapeType) {
                    0 -> drawStar(
                        center = Offset(currentX, currentY),
                        radius = pt.size * (1f - p * 0.3f),
                        color = pt.color.copy(alpha = alpha)
                    )
                    1 -> drawCircle(
                        color = pt.color.copy(alpha = alpha),
                        radius = pt.size * 0.7f * (1f - p * 0.3f),
                        center = Offset(currentX, currentY)
                    )
                    else -> drawRect(
                        color = pt.color.copy(alpha = alpha),
                        topLeft = Offset(currentX - pt.size / 2, currentY - pt.size / 2),
                        size = Size(pt.size, pt.size * 1.5f)
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawStar(center: Offset, radius: Float, color: Color) {
    val path = Path()
    val numPoints = 5
    val innerRadius = radius * 0.45f

    for (i in 0 until numPoints * 2) {
        val r = if (i % 2 == 0) radius else innerRadius
        val angle = i * Math.PI / numPoints - Math.PI / 2
        val x = center.x + (r * cos(angle)).toFloat()
        val y = center.y + (r * sin(angle)).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    drawPath(path, color)
}
