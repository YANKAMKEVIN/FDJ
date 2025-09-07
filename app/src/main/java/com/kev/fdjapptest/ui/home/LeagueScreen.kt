package com.kev.fdjapptest.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kev.domain.model.League
import com.kev.domain.model.Team
import com.kev.domain.util.Result
import com.kev.fdjapptest.ui.components.AutocompleteField
import com.kev.fdjapptest.ui.components.ErrorScreen
import com.kev.fdjapptest.ui.components.LeaguePlaceHolder
import com.kev.fdjapptest.ui.components.LeagueTopBar
import com.kev.fdjapptest.ui.components.TeamPreviewOverlay
import com.kev.fdjapptest.ui.components.TeamsContent
import com.kev.fdjapptest.ui.components.TeamsModeToggle
import com.kev.fdjapptest.ui.components.shimmer.TeamsSkeletonGrid
import com.kev.fdjapptest.ui.components.shimmer.TeamsSkeletonList

@Composable
fun LeagueScreen(viewModel: LeagueViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    LeagueScreenContent(
        state = state,
        onQueryChange = viewModel::onQueryChange,
        onSelectLeague = viewModel::onLeagueSelected,
        onDismissSuggestions = viewModel::dismissSuggestions,
        onRefresh = viewModel::onRefreshLeagues,
        onRetrySeed = { state.selectedLeague?.let(viewModel::onLeagueSelected) },
        onClearQuery = viewModel::clearQueryAndSelection,
        onDisplayModeChange = viewModel::onDisplayModeChange
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeagueScreenContent(
    state: LeagueUiState,
    onQueryChange: (String) -> Unit,
    onSelectLeague: (League) -> Unit,
    onDismissSuggestions: () -> Unit,
    onRefresh: () -> Unit,
    onRetrySeed: () -> Unit,
    onClearQuery: () -> Unit,
    onDisplayModeChange: (TeamsDisplayMode) -> Unit
) {
    val pullState = rememberPullToRefreshState()
    val listState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }
    var previewTeam by remember { mutableStateOf<Team?>(null) }

    Scaffold(
        topBar = {
            LeagueTopBar(lastUpdatedMillis = state.lastUpdatedMillis)
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = onRefresh,
            state = pullState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    AutocompleteField(
                        query = state.query,
                        suggestions = state.suggestions,
                        isOpen = state.isSuggestionsOpen,
                        onQueryChange = onQueryChange,
                        onSelect = onSelectLeague,
                        onDismiss = onDismissSuggestions,
                        onClear = onClearQuery
                    )
                    Spacer(Modifier.height(12.dp))
                    TeamsModeToggle(
                        mode = state.displayMode,
                        onChange = onDisplayModeChange
                    )
                    Spacer(Modifier.height(12.dp))

                    if (state.isTeamsLoading) {
                        if (state.displayMode == TeamsDisplayMode.Grid) {
                            TeamsSkeletonGrid(count = 10)
                        } else {
                            TeamsSkeletonList(listState = listState, count = 10)
                        }
                    } else {
                        when (val res = state.teams) {
                            null -> LeaguePlaceHolder()

                            is Result.Failure -> ErrorScreen(
                                message = "Failed to load teams.",
                                onRetry = onRetrySeed
                            )

                            is Result.Success -> {
                                TeamsContent(
                                    mode = state.displayMode,
                                    listState = listState,
                                    snapshot = res.value,
                                    onTeamClick = {},
                                    onTeamLongPress = { previewTeam = it }
                                )
                            }
                        }
                    }

                }
                if (previewTeam != null) {
                    TeamPreviewOverlay(team = previewTeam!!, onDismiss = { previewTeam = null })
                }
            }
        }
    }
}
