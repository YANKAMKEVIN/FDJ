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
                    updateState { it.copy(leagues = leagues, selectedLeague = restored) }
                }

                is Result.Failure -> updateState { it.copy(leagues = emptyList()) }
            }
        }
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

        viewModelScope.launch {
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
                        lastUpdatedMillis = System.currentTimeMillis()
                    )
                }

                is Result.Failure -> updateState { it.copy(isRefreshing = false) }
            }
        }
    }

    fun clearQueryAndSelection() {
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
