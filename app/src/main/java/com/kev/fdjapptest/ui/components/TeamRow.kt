package com.kev.fdjapptest.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.kev.domain.model.Team

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TeamRow(
    team: Team,
    modifier: Modifier = Modifier,
    onClick: (Team) -> Unit = {},
    onLongPress: (Team) -> Unit = {}
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { onClick(team) },
                onLongClick = { onLongPress(team) }
            )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SubcomposeAsyncImage(
                model = team.badgeUrl,
                contentDescription = "${team.name} badge",
                modifier = Modifier.size(40.dp),
                loading = {
                    CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp)
                },
                error = { Icon(Icons.Default.SportsSoccer, contentDescription = null) }
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(team.name, style = MaterialTheme.typography.titleMedium)
                team.badgeUrl?.let {
                    Text(
                        it,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
