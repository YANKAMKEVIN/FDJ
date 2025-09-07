package com.kev.fdjapptest.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kev.domain.model.Team

@Composable
fun TeamsList(
    teams: List<Team>,
    listState: LazyListState,
    onTeamClick: (Team) -> Unit = {},
    onTeamLongPress: (Team) -> Unit = {}
) {
    LazyColumn(state = listState, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(teams, key = { it.id }) { team ->
            TeamCard(
                team = team,
                onClick = onTeamClick,
                onLongPress = onTeamLongPress
            )
        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

