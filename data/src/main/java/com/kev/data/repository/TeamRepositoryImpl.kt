package com.kev.data.repository

import com.kev.data.datasource.remote.api.TheSportsDbApi
import com.kev.data.mapper.toDomain
import com.kev.data.util.toDomainError
import com.kev.domain.model.TeamDetails
import com.kev.domain.repository.TeamRepository
import com.kev.domain.util.DomainError
import com.kev.domain.util.Result
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

/**
 * Team details straight from the network (no cache: the screen shows a Retry on failure).
 */
class TeamRepositoryImpl @Inject constructor(
    private val api: TheSportsDbApi
) : TeamRepository {

    override suspend fun getTeamDetails(teamId: String): Result<TeamDetails> = try {
        // The API answers {"teams": null} for an unknown id; keep only the team asked for.
        val dto = api.lookupTeam(teamId).teams.orEmpty()
            .firstOrNull { it.idTeam?.trim() == teamId }
        if (dto == null || dto.teamName.isNullOrBlank()) {
            Result.Failure(DomainError.NotFound)
        } else {
            Result.Success(dto.toDomain())
        }
    } catch (e: CancellationException) {
        throw e
    } catch (t: Throwable) {
        Result.Failure(t.toDomainError(), t)
    }
}
