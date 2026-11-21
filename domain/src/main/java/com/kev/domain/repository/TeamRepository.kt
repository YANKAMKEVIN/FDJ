package com.kev.domain.repository

import com.kev.domain.model.TeamDetails
import com.kev.domain.util.Result

/** Repository contract for the details of a single team. */
interface TeamRepository {

    /** Details of the team [teamId]; [com.kev.domain.util.DomainError.NotFound] when the API knows no such team. */
    suspend fun getTeamDetails(teamId: String): Result<TeamDetails>
}
