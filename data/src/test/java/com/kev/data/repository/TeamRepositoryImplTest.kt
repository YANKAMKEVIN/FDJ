package com.kev.data.repository

import com.kev.data.datasource.remote.api.TheSportsDbApi
import com.kev.data.datasource.remote.dto.TeamDetailsDto
import com.kev.data.datasource.remote.dto.TeamDetailsResponse
import com.kev.domain.util.DomainError
import com.kev.domain.util.Result
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.IOException

/** Plain JVM tests (no Robolectric): the repository only talks to the API. */
class TeamRepositoryImplTest {

    private val api = mockk<TheSportsDbApi>()
    private val repo = TeamRepositoryImpl(api)

    private fun team(id: String, name: String?) = TeamDetailsDto(
        idTeam = id, teamName = name, badgeUrl = null, stadium = "Parc des Princes",
        location = null, formedYear = "1970", descriptionEn = null, descriptionFr = "Club parisien.",
        website = null, facebook = null, twitter = null, instagram = null, youtube = null
    )

    @Test
    fun `returns the team asked for`() = runTest {
        coEvery { api.lookupTeam("133714") } returns TeamDetailsResponse(listOf(team("133714", "Paris SG")))

        val res = repo.getTeamDetails("133714")

        val details = (res as Result.Success).value
        assertEquals("Paris SG", details.name)
        assertEquals("Parc des Princes", details.stadium)
        assertEquals(1970, details.formedYear)
    }

    @Test
    fun `an unknown id is a not found error`() = runTest {
        coEvery { api.lookupTeam("1") } returns TeamDetailsResponse(null)

        assertEquals(DomainError.NotFound, (repo.getTeamDetails("1") as Result.Failure).error)
    }

    @Test
    fun `another team in the answer is not shown as the one asked for`() = runTest {
        coEvery { api.lookupTeam("133714") } returns TeamDetailsResponse(listOf(team("133604", "Arsenal")))

        assertEquals(DomainError.NotFound, (repo.getTeamDetails("133714") as Result.Failure).error)
    }

    @Test
    fun `a network failure is a network error`() = runTest {
        coEvery { api.lookupTeam(any()) } throws IOException("offline")

        assertEquals(DomainError.Network, (repo.getTeamDetails("133714") as Result.Failure).error)
    }
}
