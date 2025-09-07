package com.kev.domain.usecase

import com.kev.domain.model.Team
import com.kev.domain.repository.LeagueRepository
import com.kev.domain.util.DispatcherProvider
import com.kev.domain.util.Result
import kotlinx.coroutines.withContext

/**
 * Returns an immediate snapshot (Free tier) after seeding Room;
 */
class GetTeamsForLeagueUseCase(
    private val repo: LeagueRepository,
    private val dispatchers: DispatcherProvider
) {
    suspend operator fun invoke(leagueName: String): Result<List<Team>> =
        withContext(dispatchers.io) { repo.getTeamsByLeagueName(leagueName) }
}