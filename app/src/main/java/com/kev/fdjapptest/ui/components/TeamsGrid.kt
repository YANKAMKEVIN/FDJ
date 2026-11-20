package com.kev.fdjapptest.ui.components

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kev.domain.model.Team

/** Size of the badge in a grid tile, shared with the loading skeleton. */
internal val GridBadgeSize = 72.dp

@OptIn(ExperimentalFoundationApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun TeamsGrid(
    teams: List<Team>,
    onTeamClick: (Team) -> Unit,
    onTeamLongPress: (Team) -> Unit = {},
    /** With [previewTeamId], lets the badge of the previewed team fly to the preview. */
    sharedScope: SharedTransitionScope? = null,
    previewTeamId: String? = null
) {
    val cascade = rememberCascadeActive(teams)
    val haptics = LocalHapticFeedback.current
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        itemsIndexed(teams, key = { _, team -> team.id }) { index, team ->
            ElevatedCard(
                modifier = Modifier
                    .animateItem()
                    .cascadeIn(index, cascade)
                    .fillMaxWidth()
                    .aspectRatio(1f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        // Inside the card, so the ripple is clipped to its rounded shape.
                        .combinedClickable(
                            onClick = { onTeamClick(team) },
                            onLongClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                onTeamLongPress(team)
                            }
                        )
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(Modifier.size(GridBadgeSize), contentAlignment = Alignment.Center) {
                        if (sharedScope == null) {
                            GridBadge(team, Modifier)
                        } else {
                            // The badge leaves its tile while the preview shows it, and comes back.
                            // Qualified: inside the tile Column, the ColumnScope overload would clash.
                            androidx.compose.animation.AnimatedVisibility(
                                visible = team.id != previewTeamId,
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                val tileScope = this
                                with(sharedScope) {
                                    GridBadge(
                                        team,
                                        Modifier.sharedElement(
                                            rememberSharedContentState(key = teamBadgeKey(team.id)),
                                            tileScope
                                        )
                                    )
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = team.name,
                        style = MaterialTheme.typography.titleSmall,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
        item {
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun GridBadge(team: Team, modifier: Modifier) {
    TeamBadge(team = team, size = GridBadgeSize, modifier = modifier)
}
