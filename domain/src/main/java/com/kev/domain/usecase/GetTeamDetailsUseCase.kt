package com.kev.domain.usecase

import com.kev.domain.model.TeamDetails
import com.kev.domain.repository.TeamRepository
import com.kev.domain.util.DispatcherProvider
import com.kev.domain.util.Result
import kotlinx.coroutines.withContext

/** Loads the details of one team (stadium, year of creation, description, links). */
class GetTeamDetailsUseCase(
    private val repo: TeamRepository,
    private val dispatchers: DispatcherProvider
) {
    suspend operator fun invoke(teamId: String): Result<TeamDetails> =
        withContext(dispatchers.io) { repo.getTeamDetails(teamId) }
}
