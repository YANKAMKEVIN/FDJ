package com.kev.fdjapptest.ui.home

import com.kev.domain.model.League
import com.kev.domain.model.Team
import com.kev.domain.util.Result

sealed interface ScreenState<out T> {
    data object Idle : ScreenState<Nothing>
    data object Loading : ScreenState<Nothing>
    data class Success<T>(val data: T) : ScreenState<T>
    data class Error(val message: String) : ScreenState<Nothing>
}

/**
 * UI state for the League screen.
 */
data class LeagueUiState(
    val query: String = "",
    val leagues: List<League> = emptyList(),
    val suggestions: List<League> = emptyList(),
    val selectedLeague: League? = null,
    val teams: Result<List<Team>>? = null,
    val isSuggestionsOpen: Boolean = false,
    val isRefreshing: Boolean = false,
    val lastUpdatedMillis: Long? = null,
    val displayMode: TeamsDisplayMode = TeamsDisplayMode.List,
    val isTeamsLoading: Boolean = false,
)

enum class TeamsDisplayMode { List, Grid }