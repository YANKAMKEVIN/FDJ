package com.kev.fdjapptest.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.kev.domain.model.Team

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TeamCard(
    team: Team,
    modifier: Modifier = Modifier,
    onClick: (Team) -> Unit = {},
    onLongPress: (Team) -> Unit = {}
) {
    val haptics = LocalHapticFeedback.current

    ElevatedCard(modifier = modifier) {
        Row(
            modifier = Modifier
                // Inside the card, so the ripple is clipped to its rounded shape.
                .combinedClickable(
                    onClick = { onClick(team) },
                    onLongClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        onLongPress(team)
                    }
                )
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TeamBadge(team = team, size = 40.dp)
            Spacer(Modifier.width(12.dp))
            Text(
                team.name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
