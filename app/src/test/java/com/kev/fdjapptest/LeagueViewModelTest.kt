package com.kev.fdjapptest

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.kev.domain.model.League
import com.kev.domain.model.Team
import com.kev.domain.usecase.GetAllLeaguesUseCase
import com.kev.domain.usecase.GetTeamsForLeagueUseCase
import com.kev.domain.usecase.RefreshLeaguesUseCase
import com.kev.domain.util.DomainError
import com.kev.domain.util.Result
import com.kev.fdjapptest.data.EPL
import com.kev.fdjapptest.data.L1
import com.kev.fdjapptest.data.TEAMS_EPL
import com.kev.fdjapptest.data.TEAMS_L1
import com.kev.fdjapptest.ui.home.LeagueViewModel
import com.kev.fdjapptest.ui.home.TeamsDisplayMode
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LeagueViewModelTest {

    @get:Rule
    val mainRule = MainDispatcherRule()

    @MockK
    lateinit var getAllLeagues: GetAllLeaguesUseCase
    @MockK
    lateinit var getTeamsForLeague: GetTeamsForLeagueUseCase
    @MockK
    lateinit var refreshLeagues: RefreshLeaguesUseCase

    private lateinit var vm: LeagueViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
    }

    private fun newVm(saved: SavedStateHandle = SavedStateHandle()): LeagueViewModel {
        return LeagueViewModel(
            getAllLeagues = getAllLeagues,
            getTeamsForLeague = getTeamsForLeague,
            refreshLeaguesUseCase = refreshLeagues,
            savedStateHandle = saved
        )
    }

    @Test
    fun `init loads leagues on success`() = runTest {
        coEvery { getAllLeagues() } returns Result.Success(listOf(EPL, L1))

        vm = newVm()
        advanceUntilIdle()

        vm.state.test {
            val s = awaitItem()
            assert(s.leagues == listOf(EPL, L1))
            cancelAndIgnoreRemainingEvents()
        }
        coVerify(exactly = 1) { getAllLeagues() }
    }

    @Test
    fun `init sets empty list when getAllLeagues fails`() = runTest {
        coEvery { getAllLeagues() } returns Result.Failure(DomainError.Network)

        vm = newVm()
        advanceUntilIdle()

        val s = vm.state.value
        assert(s.leagues.isEmpty())
    }

    @Test
    fun `onQueryChange filters suggestions case-insensitively`() = runTest {
        coEvery { getAllLeagues() } returns Result.Success(listOf(EPL, L1))

        vm = newVm()
        advanceUntilIdle()

        vm.onQueryChange("ligue")
        val s = vm.state.value
        assert(s.isSuggestionsOpen)
        assert(s.suggestions.any { it.name == L1.name })
    }

    @Test
    fun `onLeagueSelected toggles loading then exposes teams on success`() = runTest {
        coEvery { getAllLeagues() } returns Result.Success(listOf(EPL, L1))
        coEvery { getTeamsForLeague(EPL.name) } returns Result.Success(TEAMS_EPL)

        vm = newVm()
        advanceUntilIdle()

        vm.state.test {
            // initial emission
            awaitItem()

            vm.onLeagueSelected(EPL)

            // loading emission
            val loading = awaitItem()
            assert(loading.isTeamsLoading)
            assert(loading.selectedLeague == EPL)
            assert(loading.query == EPL.name)

            // loaded emission
            val loaded = awaitItem()
            assert(!loaded.isTeamsLoading)
            val teams = (loaded.teams as Result.Success<List<Team>>).value
            assert(teams == TEAMS_EPL)

            cancelAndIgnoreRemainingEvents()
        }
        coVerify { getTeamsForLeague(EPL.name) }
    }

    @Test
    fun `onLeagueSelected exposes failure when usecase fails`() = runTest {
        coEvery { getAllLeagues() } returns Result.Success(listOf(L1))
        coEvery { getTeamsForLeague(L1.name) } returns Result.Failure(DomainError.Unknown)

        vm = newVm()
        advanceUntilIdle()

        vm.onLeagueSelected(L1)
        advanceUntilIdle()

        val s = vm.state.value
        assert(!s.isTeamsLoading)
        assert(s.teams is Result.Failure)
    }

    @Test
    fun `onRefreshLeagues updates leagues and lastUpdatedMillis`() = runTest {
        coEvery { getAllLeagues() } returns Result.Success(listOf(EPL))
        coEvery { refreshLeagues() } returns Result.Success(listOf(L1))

        vm = newVm()
        advanceUntilIdle()

        vm.onRefreshLeagues()
        advanceUntilIdle()

        val s = vm.state.value
        assert(!s.isRefreshing)
        assert(s.leagues == listOf(L1))
        assert(s.lastUpdatedMillis != null)
        coVerify { refreshLeagues() }
    }

    @Test
    fun `clearQueryAndSelection resets selection and teams`() = runTest {
        coEvery { getAllLeagues() } returns Result.Success(listOf(L1))
        coEvery { getTeamsForLeague(L1.name) } returns Result.Success(TEAMS_L1)

        vm = newVm()
        advanceUntilIdle()

        vm.onLeagueSelected(L1)
        advanceUntilIdle()

        vm.clearQueryAndSelection()

        val s = vm.state.value
        assert(s.selectedLeague == null)
        assert(s.query.isEmpty())
        assert(s.teams == null)
        assert(!s.isSuggestionsOpen)
        assert(!s.isTeamsLoading)
    }

    @Test
    fun `restores selectedLeague from SavedStateHandle when leagues arrive`() = runTest {
        coEvery { getAllLeagues() } returns Result.Success(listOf(EPL, L1))

        val saved = SavedStateHandle(
            mapOf(
                "selected_league" to L1.name,
                "query" to "Li"
            )
        )
        vm = newVm(saved)
        advanceUntilIdle()

        val s = vm.state.value
        assert(s.selectedLeague?.name == L1.name)
        assert(s.query == "Li")
    }

    @Test
    fun `onQueryChange caps suggestions to 10`() = runTest {
        val leagues = (1..12).map { League("$it", "L$it", "Soccer") }
        coEvery { getAllLeagues() } returns Result.Success(leagues)
        vm = newVm(); advanceUntilIdle()

        vm.onQueryChange("L")
        val s = vm.state.value
        assert(s.suggestions.size == 10)
        assert(s.isSuggestionsOpen)
    }

    @Test
    fun `onDisplayModeChange updates mode`() = runTest {
        coEvery { getAllLeagues() } returns Result.Success(listOf())
        vm = newVm(); advanceUntilIdle()

        vm.onDisplayModeChange(TeamsDisplayMode.Grid)
        assert(vm.state.value.displayMode == TeamsDisplayMode.Grid)
    }

    @Test
    fun `onRefreshLeagues failure resets isRefreshing without touching lastUpdated`() = runTest {
        coEvery { getAllLeagues() } returns Result.Success(listOf())
        coEvery { refreshLeagues() } returns Result.Failure(DomainError.Network)
        vm = newVm(); advanceUntilIdle()

        vm.onRefreshLeagues(); advanceUntilIdle()
        val s = vm.state.value
        assert(!s.isRefreshing)
        assert(s.lastUpdatedMillis == null)
    }

}
