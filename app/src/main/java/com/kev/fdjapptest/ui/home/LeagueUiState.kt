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

    /** The user typed something that matches no loaded league: say so instead of closing the menu. */
    val showNoSuggestionMatch: Boolean
        get() = isSuggestionsOpen && query.isNotBlank() && leagues.isNotEmpty() && suggestions.isEmpty()

    /** Number of teams shown, or null while they are loading / not loaded. */
    val teamsCount: Int? get() = (teams as? Result.Success)?.value?.size

    /** The teams area has a league to title: its teams are loading or loaded. */
    val showTeamsHeader: Boolean
        get() = selectedLeague != null && (isTeamsLoading || teams is Result.Success)

    /** The Grid/List toggle only matters when there are teams to show (or about to be). */
    val showDisplayModeToggle: Boolean
        get() = isTeamsLoading || (teamsCount ?: 0) > 0
}

enum class TeamsDisplayMode { List, Grid }