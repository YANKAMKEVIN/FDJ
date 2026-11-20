package com.kev.fdjapptest.ui.components.shimmer

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    cornerRadius: Float = 12f,
    isCircle: Boolean = false
) {
    var size by remember { mutableStateOf(IntSize.Zero) }
    val transition = rememberInfiniteTransition(label = "shimmer")
    val animatedX by transition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1300, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerX"
    )

    // Colors for the skeleton sweep, from the theme so the skeleton stays soft in dark mode
    val base = MaterialTheme.colorScheme.surfaceContainerHighest
    val highlight = MaterialTheme.colorScheme.surfaceContainerLowest

    val brush = remember(size, animatedX, base, highlight) {
        val width = size.width.coerceAtLeast(1)
        val height = size.height.coerceAtLeast(1)
        // Animate a diagonal gradient from left-top to right-bottom
        val startX = animatedX * width
        val startY = animatedX * height
        Brush.linearGradient(
            colors = listOf(base, highlight, base),
            start = Offset(startX, startY),
            end = Offset(startX + width.toFloat(), startY + height.toFloat())
        )
    }

    Box(
        modifier = modifier
            .onGloballyPositioned { size = it.size }
            .clip(if (isCircle) CircleShape else RoundedCornerShape(cornerRadius.dp))
            .background(brush)
    )
}
