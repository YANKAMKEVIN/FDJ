package com.kev.fdjapptest

import com.kev.domain.model.Team
import com.kev.domain.model.TeamDetails
import com.kev.domain.model.TeamLink
import com.kev.domain.usecase.GetTeamDetailsUseCase
import com.kev.domain.util.DomainError
import com.kev.domain.util.Result
import com.kev.fdjapptest.ui.detail.TeamDetailUiState
import com.kev.fdjapptest.ui.detail.TeamDetailViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TeamDetailViewModelTest {

    @get:Rule
    val mainRule = MainDispatcherRule()

    private val getTeamDetails = mockk<GetTeamDetailsUseCase>()
    private val vm by lazy { TeamDetailViewModel(getTeamDetails) }

    private val arsenal = Team("t1", "Arsenal", "https://badge/arsenal.png")
    private val chelsea = Team("t2", "Chelsea", null)

    private fun details(id: String, name: String) = TeamDetails(
        id = id, name = name, badgeUrl = null, stadium = "Emirates Stadium", location = null,
        formedYear = 1886, descriptionEn = "EN", descriptionFr = null,
        links = listOf(TeamLink(TeamLink.Kind.Website, "https://www.arsenal.com"))
    )

    @Test
    fun `opening a team shows its name at once and loads its details`() = runTest {
        val answer = CompletableDeferred<Result<TeamDetails>>()
        coEvery { getTeamDetails("t1") } coAnswers { answer.await() }

        vm.open(arsenal)

        with(vm.state.value) {
            assertTrue(isLoading)
            assertEquals("Arsenal", name)
            assertEquals("https://badge/arsenal.png", badgeUrl)
        }

        answer.complete(Result.Success(details("t1", "Arsenal")))
        advanceUntilIdle()

        with(vm.state.value) {
            assertFalse(isLoading)
            assertNull(error)
            assertEquals("Emirates Stadium", details?.stadium)
            // The list badge stays when the details have none.
            assertEquals("https://badge/arsenal.png", badgeUrl)
        }
    }

    @Test
    fun `a failure shows the error and retry loads again`() = runTest {
        coEvery { getTeamDetails("t1") } returns Result.Failure(DomainError.Network)

        vm.open(arsenal)
        advanceUntilIdle()

        assertEquals(DomainError.Network, vm.state.value.error)
        assertFalse(vm.state.value.isLoading)

        coEvery { getTeamDetails("t1") } returns Result.Success(details("t1", "Arsenal"))
        vm.retry()
        advanceUntilIdle()

        assertNull(vm.state.value.error)
        assertEquals(1886, vm.state.value.details?.formedYear)
        coVerify(exactly = 2) { getTeamDetails("t1") }
    }

    @Test
    fun `reopening the same loaded team does not reload it`() = runTest {
        coEvery { getTeamDetails("t1") } returns Result.Success(details("t1", "Arsenal"))

        vm.open(arsenal)
        advanceUntilIdle()
        vm.open(arsenal)
        advanceUntilIdle()

        coVerify(exactly = 1) { getTeamDetails("t1") }
    }

    @Test
    fun `a slow answer for the previous team does not replace the team now shown`() = runTest {
        val slowArsenal = CompletableDeferred<Result<TeamDetails>>()
        coEvery { getTeamDetails("t1") } coAnswers { slowArsenal.await() }
        coEvery { getTeamDetails("t2") } returns Result.Success(details("t2", "Chelsea"))

        vm.open(arsenal)
        vm.open(chelsea)
        slowArsenal.complete(Result.Success(details("t1", "Arsenal")))
        advanceUntilIdle()

        assertEquals("Chelsea", vm.state.value.name)
        assertEquals("t2", vm.state.value.details?.id)
    }

    @Test
    fun `a team without any information is reported as such`() {
        val empty = TeamDetails(
            id = "t1", name = "Arsenal", badgeUrl = null, stadium = null, location = null,
            formedYear = null, descriptionEn = null, descriptionFr = null, links = emptyList()
        )
        assertTrue(TeamDetailUiState(team = arsenal, details = empty).hasNoInformation)
        assertFalse(TeamDetailUiState(team = arsenal, details = empty.copy(formedYear = 1886)).hasNoInformation)
        // Nothing loaded yet is not "no information".
        assertFalse(TeamDetailUiState(team = arsenal, isLoading = true).hasNoInformation)
    }
}
