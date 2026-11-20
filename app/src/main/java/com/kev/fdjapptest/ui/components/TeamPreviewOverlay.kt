package com.kev.fdjapptest.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.kev.domain.model.Team
import com.kev.fdjapptest.R

@Composable
fun TeamPreviewOverlay(
    team: Team,
    onDismiss: () -> Unit,
    visible: Boolean = true,
    fancyMotion: Boolean = true,
    shimmer: Boolean = true
) {
    // Starts hidden so the entrance always plays, and stays composed while hiding so the exit
    // animation (fade + shrink) is played instead of the overlay vanishing.
    val visibility = remember { MutableTransitionState(false) }
    visibility.targetState = visible
    val closeLabel = stringResource(R.string.team_preview_close)

    // Back closes the preview instead of leaving the app.
    BackHandler(enabled = visible, onBack = onDismiss)

    AnimatedVisibility(
        visibleState = visibility,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        val scale = remember { Animatable(0.6f) }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.45f))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClickLabel = closeLabel
                ) { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            val transition = rememberInfiniteTransition(label = "previewMotion")

            val tilt by if (fancyMotion) {
                transition.animateFloat(
                    initialValue = -2f, targetValue = 2f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1600, easing = LinearOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "tilt"
                )
            } else remember { mutableFloatStateOf(0f) }

            val shimmerOffset by if (shimmer) {
                transition.animateFloat(
                    initialValue = -300f, targetValue = 300f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1400, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "shimmer"
                )
            } else remember { mutableFloatStateOf(0f) }

            val shimmerBrush = remember(shimmerOffset, shimmer) {
                if (!shimmer) null else Brush.linearGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.White.copy(alpha = 0.25f),
                        Color.Transparent
                    ),
                    start = Offset(shimmerOffset, shimmerOffset),
                    end = Offset(shimmerOffset + 220f, shimmerOffset + 220f)
                )
            }

            // Rebound "pop" at entrance
            LaunchedEffect(Unit) {
                scale.animateTo(
                    targetValue = 1.08f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
                scale.animateTo(1f, animationSpec = tween(120))
            }

            Surface(
                shape = RoundedCornerShape(20.dp),
                tonalElevation = 8.dp,
                shadowElevation = 24.dp,
                modifier = Modifier
                    .animateEnterExit(
                        enter = EnterTransition.None,
                        exit = scaleOut(
                            targetScale = 0.85f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        )
                    )
                    // Announced by TalkBack as a pane named after the team, like a dialog.
                    .semantics { paneTitle = team.name }
                    .graphicsLayer {
                        scaleX = scale.value
                        scaleY = scale.value
                        rotationZ = tilt
                    }
            ) {
                Column(
                    modifier = Modifier
                        .width(260.dp)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .size(180.dp)
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        SubcomposeAsyncImage(
                            model = team.badgeUrl,
                            contentDescription = "${team.name} badge",
                            modifier = Modifier.size(140.dp),
                            loading = {
                                CircularProgressIndicator(Modifier.size(24.dp), strokeWidth = 2.dp)
                            },
                            error = {
                                Icon(
                                    Icons.Default.SportsSoccer,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                        )
                        if (shimmerBrush != null) {
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .background(shimmerBrush, RoundedCornerShape(16.dp))
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = team.name,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
