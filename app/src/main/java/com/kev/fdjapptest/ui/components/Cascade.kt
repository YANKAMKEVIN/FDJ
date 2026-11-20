package com.kev.fdjapptest.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/** Delay between two items of the cascade, and the number of items that wait their turn. */
private const val CASCADE_STEP_MS = 40L
private const val CASCADE_MAX_STEPS = 10

/**
 * True during the first moments after [key] (a new team list) is shown: items composed in that
 * window fade in one after the other; items scrolled into view later appear without delay.
 */
@Composable
fun rememberCascadeActive(key: Any?): Boolean {
    var active by remember(key) { mutableStateOf(true) }
    LaunchedEffect(key) {
        delay(CASCADE_STEP_MS * CASCADE_MAX_STEPS + 300)
        active = false
    }
    return active
}

/** Fade + short rise of the item at [index], staggered while [active]. */
@Composable
fun Modifier.cascadeIn(index: Int, active: Boolean): Modifier {
    val progress = remember { Animatable(if (active) 0f else 1f) }
    LaunchedEffect(Unit) {
        if (progress.value < 1f) {
            delay(CASCADE_STEP_MS * index.coerceAtMost(CASCADE_MAX_STEPS))
            progress.animateTo(1f, tween(durationMillis = 260))
        }
    }
    return graphicsLayer {
        alpha = progress.value
        translationY = (1f - progress.value) * 16.dp.toPx()
    }
}
