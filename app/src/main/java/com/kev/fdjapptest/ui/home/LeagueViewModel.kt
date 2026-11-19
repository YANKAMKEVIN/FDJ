package com.kev.fdjapptest.ui.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kev.domain.model.League
import com.kev.domain.usecase.GetAllLeaguesUseCase
import com.kev.domain.usecase.GetTeamsForLeagueUseCase
import com.kev.domain.usecase.RefreshLeaguesUseCase
import com.kev.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeagueViewModel @Inject constructor(
    private val getAllLeagues: GetAllLeaguesUseCase,
    private val getTeamsForLeague: GetTeamsForLeagueUseCase,
    private val refreshLeaguesUseCase: RefreshLeaguesUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private companion object {
        const val KEY_QUERY = "query"
        const val KEY_SELECTED_LEAGUE = "selected_league"
        const val KEY_LAST_UPDATED = "last_updated"
    }

    private val _state = MutableStateFlow(
        LeagueUiState(
            query = savedStateHandle[KEY_QUERY] ?: "",
            selectedLeague = null,
            lastUpdatedMillis = savedStateHandle[KEY_LAST_UPDATED]
        )
    )
    val state = _state.asStateFlow()

    /** Teams request of the current selection; cancelled when the selection changes. */
    private var teamsJob: Job? = null

    init {
        loadLeaguesIfNeeded()
    }

    private fun loadLeaguesIfNeeded() {
        if (_state.value.leagues.isNotEmpty()) return
        viewModelScope.launch {
            when (val res = getAllLeagues()) {
                is Result.Success -> {
                    val leagues = res.value
                    val savedName: String? = savedStateHandle[KEY_SELECTED_LEAGUE]
                    val restored =
                        savedName?.let { name -> leagues.firstOrNull { it.name == name } }
                    // After process death only the league name survives in the SavedStateHandle:
                    // reload its teams instead of leaving the restored selection with an empty screen.
                    updateState {
                        it.copy(
                            leagues = leagues,
                            selectedLeague = restored,
                            leaguesError = null,
                            isTeamsLoading = restored != null
                        )
                    }
                    restored?.let(::fetchTeams)
                }

                is Result.Failure -> updateState {
                    it.copy(leagues = emptyList(), leaguesError = res.error)
                }
            }
        }
    }

    /** Retry action of the "leagues could not be loaded" error. */
    fun retryLoadLeagues() {
        updateState { it.copy(leaguesError = null) }
        loadLeaguesIfNeeded()
    }

    fun onQueryChange(text: String) {
        updateState {
            it.copy(
                query = text,
                isSuggestionsOpen = true,
                suggestions = it.leagues
                    .filter { lg -> lg.name.contains(text, ignoreCase = true) }
                    .take(10)
            )
        }
    }

    fun onDisplayModeChange(mode: TeamsDisplayMode) {
        updateState { it.copy(displayMode = mode) }
    }

    fun dismissSuggestions() {
        updateState { it.copy(isSuggestionsOpen = false) }
    }

    fun onLeagueSelected(league: League) {
        updateState {
            it.copy(
                selectedLeague = league,
                query = league.name,
                isSuggestionsOpen = false,
                teams = null,
                isTeamsLoading = true
            )
        }
        fetchTeams(league)
    }

    private fun fetchTeams(league: League) {
        // Cancel the previous request: a slow answer for an older league must not overwrite the
        // teams of the league now selected.
        teamsJob?.cancel()
        teamsJob = viewModelScope.launch {
            when (val res = getTeamsForLeague(league.name)) {
                is Result.Success -> {
                    updateState { it.copy(teams = res, isTeamsLoading = false) }
                }

                is Result.Failure -> updateState { it.copy(teams = res, isTeamsLoading = false) }
            }
        }
    }

    fun onRefreshLeagues() {
        updateState { it.copy(isRefreshing = true) }
        viewModelScope.launch {
            when (val res = refreshLeaguesUseCase()) {
                is Result.Success -> updateState {
                    it.copy(
                        leagues = res.value,
                        isRefreshing = false,
                        lastUpdatedMillis = System.currentTimeMillis(),
                        leaguesError = null
                    )
                }

                is Result.Failure -> updateState {
                    // Keep the leagues already shown; only report the error when there is nothing to search.
                    it.copy(
                        isRefreshing = false,
                        leaguesError = if (it.leagues.isEmpty()) res.error else it.leaguesError
                    )
                }
            }
        }
    }

    fun clearQueryAndSelection() {
        teamsJob?.cancel()
        updateState {
            it.copy(
                query = "",
                selectedLeague = null,
                teams = null,
                suggestions = emptyList(),
                isSuggestionsOpen = false,
                isTeamsLoading = false
            )
        }
    }

    private fun updateState(reducer: (LeagueUiState) -> LeagueUiState) {
        val newState = reducer(_state.value)
        savedStateHandle[KEY_QUERY] = newState.query
        savedStateHandle[KEY_LAST_UPDATED] = newState.lastUpdatedMillis
        savedStateHandle[KEY_SELECTED_LEAGUE] = newState.selectedLeague?.name
        _state.value = newState
    }
}
