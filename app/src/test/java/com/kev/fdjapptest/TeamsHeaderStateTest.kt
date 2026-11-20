package com.kev.fdjapptest

import com.kev.domain.model.League
import com.kev.domain.model.Team
import com.kev.domain.util.DomainError
import com.kev.domain.util.Result
import com.kev.fdjapptest.ui.home.LeagueUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TeamsHeaderStateTest {

    private val l1 = League("1", "French Ligue 1", "Soccer")
    private val teams = listOf(Team("a", "Lyon", null), Team("b", "Lille", null))

    @Test
    fun `header shows the league and the number of loaded teams`() {
        val s = LeagueUiState(selectedLeague = l1, teams = Result.Success(teams))
        assertTrue(s.showTeamsHeader)
        assertEquals(2, s.teamsCount)
    }

    @Test
    fun `header shows the league without count while loading`() {
        val s = LeagueUiState(selectedLeague = l1, isTeamsLoading = true)
        assertTrue(s.showTeamsHeader)
        assertNull(s.teamsCount)
    }

    @Test
    fun `no header without selection or when the teams failed`() {
        assertFalse(LeagueUiState().showTeamsHeader)
        assertFalse(LeagueUiState(selectedLeague = l1, teams = Result.Failure(DomainError.Network)).showTeamsHeader)
    }

    @Test
    fun `toggle shows only while teams load or when there are teams`() {
        assertFalse(LeagueUiState().showDisplayModeToggle)
        assertTrue(LeagueUiState(selectedLeague = l1, isTeamsLoading = true).showDisplayModeToggle)
        assertTrue(LeagueUiState(selectedLeague = l1, teams = Result.Success(teams)).showDisplayModeToggle)
        assertFalse(LeagueUiState(selectedLeague = l1, teams = Result.Success(emptyList())).showDisplayModeToggle)
        assertFalse(LeagueUiState(selectedLeague = l1, teams = Result.Failure(DomainError.Network)).showDisplayModeToggle)
    }
}
