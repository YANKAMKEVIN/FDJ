package com.kev.fdjapptest.ui.components

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.kev.domain.model.Team
import com.kev.fdjapptest.R
import com.kev.fdjapptest.ui.home.TeamsDisplayMode

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun TeamsContent(
    mode: TeamsDisplayMode,
    snapshot: List<Team>,
    listState: LazyListState,
    onTeamClick: (Team) -> Unit = {},
    onTeamLongPress: (Team) -> Unit = {},
    sharedScope: SharedTransitionScope? = null,
    previewTeamId: String? = null
) {
    if (snapshot.isEmpty()) {
        LeaguePlaceHolder(
            title = stringResource(R.string.league_placeholder_empty_title),
            subtitle = stringResource(R.string.league_placeholder_subtitle),
        )
        return
    }
    when (mode) {
        TeamsDisplayMode.Grid ->
            TeamsGrid(
                teams = snapshot,
                onTeamClick = onTeamClick,
                onTeamLongPress = onTeamLongPress,
                sharedScope = sharedScope,
                previewTeamId = previewTeamId
            )

        TeamsDisplayMode.List ->
            TeamsList(
                teams = snapshot,
                listState = listState,
                onTeamClick = onTeamClick,
                onTeamLongPress = onTeamLongPress
            )
    }
}
