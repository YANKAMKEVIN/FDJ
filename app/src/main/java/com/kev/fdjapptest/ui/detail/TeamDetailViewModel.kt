package com.kev.fdjapptest.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kev.domain.model.Team
import com.kev.domain.usecase.GetTeamDetailsUseCase
import com.kev.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeamDetailViewModel @Inject constructor(
    private val getTeamDetails: GetTeamDetailsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(TeamDetailUiState())
    val state: StateFlow<TeamDetailUiState> = _state.asStateFlow()

    private var loadJob: Job? = null

    /**
     * Shows [team] and loads its details. Opening the same team again keeps what is already
     * loaded (or loading); another team replaces it.
     */
    fun open(team: Team) {
        val current = _state.value
        if (current.team?.id == team.id && current.error == null &&
            (current.isLoading || current.details != null)
        ) return
        _state.value = TeamDetailUiState(team = team, isLoading = true)
        load(team.id)
    }

    /** Retry action of the error screen. */
    fun retry() {
        val team = _state.value.team ?: return
        _state.value = _state.value.copy(isLoading = true, error = null)
        load(team.id)
    }

    private fun load(teamId: String) {
        // A slow answer for a previous team must not replace the team now shown.
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            val res = getTeamDetails(teamId)
            _state.value = when (res) {
                is Result.Success -> _state.value.copy(isLoading = false, details = res.value, error = null)
                is Result.Failure -> _state.value.copy(isLoading = false, details = null, error = res.error)
            }
        }
    }
}
