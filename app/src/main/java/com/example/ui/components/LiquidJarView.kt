package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ColorSegment
import com.example.model.Jar
import com.example.model.JarSkinTheme
import com.example.model.JarType
import kotlin.math.sin

/**
 * Custom 3D Realistic Glass Jar with layered animated liquids, meniscus curvature,
 * glass specular reflections, locked/frozen overlays, and selection states.
 */
@Composable
fun LiquidJarView(
    jar: Jar,
    jarSkin: JarSkinTheme,
    isColorblindMode: Boolean,
    onJarClick: () -> Unit,
    modifier: Modifier = Modifier,
    width: Dp = 68.dp,
    height: Dp = 190.dp,
    pourProgress: Float = 0f, // 0 = resting, 1 = maximum pour
    tiltAngle: Float = 0f
) {
    // Lift animation when selected
    val liftOffset by animateFloatAsState(
        targetValue = if (jar.isSelected) -24f else 0f,
        animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
        label = "jarLift"
    )

    val scaleState by animateFloatAsState(
        targetValue = if (jar.isSelected) 1.05f else 1f,
        animationSpec = tween(durationMillis = 180),
        label = "jarScale"
    )

    // Ambient liquid bubble wave oscillation
    val infiniteTransition = rememberInfiniteTransition(label = "liquidWave")
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wavePhase"
    )

    val rainbowHue by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rainbowHue"
    )

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .testTag("jar_${jar.id}")
            .width(width)
            .height(height)
            .offset(y = liftOffset.dp)
            .scale(scaleState)
            .rotate(tiltAngle)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onJarClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasW = size.width
            val canvasH = size.height

            val jarLeft = 6f
            val jarRight = canvasW - 6f
            val jarTop = 16f
            val jarBottom = canvasH - 8f
            val jarInnerW = jarRight - jarLeft
            val jarInnerH = jarBottom - jarTop

            val neckRadius = 14f
            val bottomRadius = 26f // rounded-b-3xl deep glass curvature

            // Jar Container Outer Path
            val jarPath = Path().apply {
                addRoundRect(
                    RoundRect(
                        left = jarLeft,
                        top = jarTop,
                        right = jarRight,
                        bottom = jarBottom,
                        topLeftCornerRadius = CornerRadius(neckRadius, neckRadius),
                        topRightCornerRadius = CornerRadius(neckRadius, neckRadius),
                        bottomLeftCornerRadius = CornerRadius(bottomRadius, bottomRadius),
                        bottomRightCornerRadius = CornerRadius(bottomRadius, bottomRadius)
                    )
                )
            }

            // 1. Draw Outer Glow Ring for Selected State (Immersive UI: ring-4 ring-white/10 shadow-[0_0_30px_rgba(59,130,246,0.3)])
            if (jar.isSelected) {
                drawRoundRect(
                    color = Color(0x333B82F6), // Blue-500 glow halo
                    topLeft = Offset(jarLeft - 5f, jarTop - 5f),
                    size = Size(jarInnerW + 10f, jarInnerH + 10f),
                    cornerRadius = CornerRadius(bottomRadius + 4f, bottomRadius + 4f)
                )
                drawRoundRect(
                    color = Color(0x26FFFFFF), // ring-4 ring-white/10
                    topLeft = Offset(jarLeft - 3f, jarTop - 3f),
                    size = Size(jarInnerW + 6f, jarInnerH + 6f),
                    cornerRadius = CornerRadius(bottomRadius + 2f, bottomRadius + 2f)
                )
            }

            // 2. Draw Jar Drop Shadow
            drawRoundRect(
                color = Color.Black.copy(alpha = 0.35f),
                topLeft = Offset(jarLeft + 2f, jarTop + 4f),
                size = Size(jarInnerW, jarInnerH),
                cornerRadius = CornerRadius(bottomRadius, bottomRadius)
            )

            // 3. Draw Jar Interior Glass Background (bg-white/5 to bg-white/10 gradient)
            val glassBgBrush = Brush.verticalGradient(
                colors = if (jar.isSelected) {
                    listOf(Color(0x33FFFFFF), Color(0x1A3B82F6))
                } else {
                    listOf(Color(0x0DFFFFFF), Color(0x1AFFFFFF))
                },
                startY = jarTop,
                endY = jarBottom
            )
            drawPath(path = jarPath, brush = glassBgBrush)

            // 3. Draw Liquid Layers (Clipped strictly inside the jar)
            clipPath(jarPath) {
                val capacity = jar.capacity
                val segmentHeight = jarInnerH / capacity

                // Draw each segment from bottom to top
                jar.segments.forEachIndexed { index, segment ->
                    val segBottom = jarBottom - (index * segmentHeight)
                    val segTop = segBottom - segmentHeight

                    if (segment.isHidden) {
                        // Mystery Segment: Dark translucent gradient with mystery glyph
                        drawRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFF1E293B), Color(0xFF0F172A)),
                                startY = segTop,
                                endY = segBottom
                            ),
                            topLeft = Offset(jarLeft, segTop),
                            size = Size(jarInnerW, segmentHeight)
                        )
                        // Mystery shimmer line
                        drawLine(
                            color = Color(0x33FFFFFF),
                            start = Offset(jarLeft + 4f, segTop + (segmentHeight / 2)),
                            end = Offset(jarRight - 4f, segTop + (segmentHeight / 2)),
                            strokeWidth = 1.5f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f))
                        )
                    } else {
                        // Liquid Color
                        val baseColor = segment.color.primaryColor
                        val darkColor = segment.color.darkColor
                        val lightColor = segment.color.lightColor

                        val liquidBrush = Brush.verticalGradient(
                            colors = listOf(lightColor, baseColor, darkColor),
                            startY = segTop,
                            endY = segBottom
                        )

                        // If it's the top visible segment, add subtle wave meniscus
                        val isTopVisible = (index == jar.segments.lastIndex)
                        if (isTopVisible) {
                            val wavePath = Path().apply {
                                moveTo(jarLeft, segBottom)
                                lineTo(jarLeft, segTop + 4f)
                                // Sine wave curve
                                val midX = jarLeft + jarInnerW / 2
                                val waveOffset = sin(wavePhase) * 2.5f
                                quadraticTo(
                                    midX, segTop - 3f + waveOffset,
                                    jarRight, segTop + 4f
                                )
                                lineTo(jarRight, segBottom)
                                close()
                            }
                            drawPath(wavePath, liquidBrush)
                        } else {
                            drawRect(
                                brush = liquidBrush,
                                topLeft = Offset(jarLeft, segTop),
                                size = Size(jarInnerW, segmentHeight)
                            )
                        }

                        // Subtle horizontal liquid layer separator
                        if (index > 0) {
                            drawLine(
                                color = Color.White.copy(alpha = 0.25f),
                                start = Offset(jarLeft + 2f, segBottom),
                                end = Offset(jarRight - 2f, segBottom),
                                strokeWidth = 1.2f
                            )
                        }

                        // Frozen Ice overlay
                        if (segment.isFrozen) {
                            drawRect(
                                color = Color(0x88E0F7FA),
                                topLeft = Offset(jarLeft, segTop),
                                size = Size(jarInnerW, segmentHeight)
                            )
                            // Frost lines
                            drawLine(
                                color = Color.White.copy(alpha = 0.8f),
                                start = Offset(jarLeft + 6f, segTop + 6f),
                                end = Offset(jarRight - 6f, segBottom - 6f),
                                strokeWidth = 2f
                            )
                            drawLine(
                                color = Color.White.copy(alpha = 0.8f),
                                start = Offset(jarRight - 6f, segTop + 6f),
                                end = Offset(jarLeft + 6f, segBottom - 6f),
                                strokeWidth = 2f
                            )
                        }
                    }
                }

                // Rainbow Jar animated rainbow background glow if active
                if (jar.type == JarType.RAINBOW && jar.rainbowRemainingPours > 0) {
                    val rainbowColors = listOf(
                        Color(0xFFFF0080), Color(0xFFFF8C00), Color(0xFFFFEE00),
                        Color(0xFF00F5D4), Color(0xFF00BFFF), Color(0xFFB388FF)
                    )
                    drawRoundRect(
                        brush = Brush.sweepGradient(rainbowColors, center = Offset(canvasW / 2, canvasH / 2)),
                        topLeft = Offset(jarLeft, jarTop),
                        size = Size(jarInnerW, jarInnerH),
                        cornerRadius = CornerRadius(bottomRadius, bottomRadius),
                        alpha = 0.15f
                    )
                }

                // 4. Glass Specular Highlights (Realistic 3D Reflection)
                // Left sharp curved light sheen
                drawRoundRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.65f),
                            Color.White.copy(alpha = 0.2f),
                            Color.Transparent
                        ),
                        startX = jarLeft + 3f,
                        endX = jarLeft + 14f
                    ),
                    topLeft = Offset(jarLeft + 4f, jarTop + 8f),
                    size = Size(8f, jarInnerH - 20f),
                    cornerRadius = CornerRadius(4f, 4f)
                )

                // Right soft rim glow
                drawRoundRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.3f)
                        ),
                        startX = jarRight - 10f,
                        endX = jarRight - 2f
                    ),
                    topLeft = Offset(jarRight - 8f, jarTop + 8f),
                    size = Size(6f, jarInnerH - 20f),
                    cornerRadius = CornerRadius(3f, 3f)
                )
            }

            // 5. Jar Glass Border & Lip Rim
            val strokeColor = if (jar.isSelected) {
                Color(0xFF00F5D4) // Bright Cyan neon selection ring
            } else if (jar.isCompleted) {
                Color(0xFFFFD700) // Golden completed sheen
            } else {
                Color(jarSkin.glassBorderColorHex)
            }

            val strokeWidth = if (jar.isSelected) 4f else 2.5f

            // Outer Jar Rim Outline
            drawPath(
                path = jarPath,
                color = strokeColor,
                style = Stroke(width = strokeWidth)
            )

            // Top Lip Rim of the glass tube
            val lipLeft = jarLeft - 3f
            val lipRight = jarRight + 3f
            val lipHeight = 8f
            drawRoundRect(
                color = strokeColor,
                topLeft = Offset(lipLeft, jarTop - 4f),
                size = Size(lipRight - lipLeft, lipHeight),
                cornerRadius = CornerRadius(3f, 3f),
                style = Stroke(width = strokeWidth)
            )

            // 6. Special Overlays: Locked / One-Way / Rainbow Icons
            if (jar.isLocked) {
                // Dim locked glass
                drawRoundRect(
                    color = Color.Black.copy(alpha = 0.5f),
                    topLeft = Offset(jarLeft, jarTop),
                    size = Size(jarInnerW, jarInnerH),
                    cornerRadius = CornerRadius(bottomRadius, bottomRadius)
                )
            }
        }

        // Colorblind overlay glyphs & Special Icons centered per segment
        Box(modifier = Modifier.fillMaxSize()) {
            val segmentHeightDp = (height.value - 24f) / jar.capacity

            jar.segments.forEachIndexed { index, segment ->
                val topPadding = (height.value - 12f) - ((index + 1) * segmentHeightDp)
                if (segment.isHidden) {
                    Text(
                        text = "?",
                        color = Color(0xFF94A3B8),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = topPadding.dp + 4.dp)
                    )
                } else if (isColorblindMode) {
                    Text(
                        text = segment.color.symbol,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = topPadding.dp + 6.dp)
                    )
                }
            }

            // Droplet indicator for active pouring jar
            if (jar.isSelected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset(y = (-8).dp)
                        .width(4.dp)
                        .height(14.dp)
                        .background(Color(0x80FFFFFF), CircleShape)
                )
            }

            // Locked icon overlay
            if (jar.isLocked) {
                Text(
                    text = "🔒",
                    fontSize = 22.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // Rainbow jar badge
            if (jar.type == JarType.RAINBOW && jar.rainbowRemainingPours > 0) {
                Text(
                    text = "🌈",
                    fontSize = 18.sp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 4.dp, top = 2.dp)
                )
            }

            // One-Way Out Funnel
            if (jar.type == JarType.ONE_WAY_OUT) {
                Text(
                    text = "⬆",
                    color = Color(0xFFFF6D00),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 2.dp)
                )
            }

            // One-Way In Funnel
            if (jar.type == JarType.ONE_WAY_IN) {
                Text(
                    text = "⬇",
                    color = Color(0xFF00E5FF),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 2.dp)
                )
            }
        }
    }
}
