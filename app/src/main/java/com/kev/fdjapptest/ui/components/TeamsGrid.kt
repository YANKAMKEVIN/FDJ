package com.kev.fdjapptest.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.kev.domain.model.Team

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TeamsGrid(
    teams: List<Team>,
    onTeamClick: (Team) -> Unit,
    onTeamLongPress: (Team) -> Unit = {}
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(teams, key = { it.id }) { team ->
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .combinedClickable(
                        onClick = { onTeamClick(team) },
                        onLongClick = { onTeamLongPress(team) }
                    )
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    SubcomposeAsyncImage(
                        model = team.badgeUrl,
                        contentDescription = "${team.name} badge",
                        modifier = Modifier.size(56.dp),
                        loading = {
                            CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                        },
                        error = { Icon(Icons.Default.SportsSoccer, contentDescription = null) }
                    )
                }
            }
        }
        item {
            Spacer(Modifier.height(12.dp))
        }
    }
}

