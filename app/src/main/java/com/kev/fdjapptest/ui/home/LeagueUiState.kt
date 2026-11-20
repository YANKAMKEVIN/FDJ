package com.kev.fdjapptest.ui.home

import com.kev.domain.model.League
import com.kev.domain.model.Team
import com.kev.domain.util.DomainError
import com.kev.domain.util.Result

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
    val displayMode: TeamsDisplayMode = TeamsDisplayMode.Grid,
    val isTeamsLoading: Boolean = false,
    /** Set when the league list could not be loaded (the search cannot suggest anything). */
    val leaguesError: DomainError? = null,
) {
    /** Well-known leagues offered as one-tap shortcuts before anything is typed. */
    val popularLeagues: List<League> get() = popularLeaguesOf(leagues)
}

enum class TeamsDisplayMode { List, Grid }