package com.kev.fdjapptest.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.kev.domain.model.Team

/**
 * Team badge: a calm surfaceVariant disc while it loads (instead of a spinner per tile),
 * then the image fades in.
 */
@Composable
fun TeamBadge(
    team: Team,
    size: Dp,
    modifier: Modifier = Modifier
) {
    SubcomposeAsyncImage(
        model = team.badgeUrl,
        contentDescription = "${team.name} badge",
        modifier = modifier.size(size),
        loading = {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
            )
        },
        success = {
            val alpha = remember { Animatable(0f) }
            LaunchedEffect(Unit) { alpha.animateTo(1f, tween(durationMillis = 220)) }
            SubcomposeAsyncImageContent(Modifier.graphicsLayer { this.alpha = alpha.value })
        },
        error = {
            Icon(
                Icons.Default.SportsSoccer,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    )
}
