package com.kev.fdjapptest

import com.kev.domain.model.League
import com.kev.fdjapptest.ui.home.LeagueUiState
import com.kev.fdjapptest.ui.home.popularLeaguesOf
import org.junit.Assert.assertEquals
import org.junit.Test

class PopularLeaguesTest {

    private val epl = League("1", "English Premier League", "Soccer")
    private val l1 = League("2", "French Ligue 1", "Soccer")
    private val nba = League("3", "NBA", "Basketball")
    private val other = League("4", "Scottish Premier League", "Soccer")

    @Test
    fun `keeps only loaded popular leagues in the curated order`() {
        assertEquals(listOf(epl, l1, nba), popularLeaguesOf(listOf(nba, other, l1, epl)))
    }

    @Test
    fun `matches names case-insensitively`() {
        val lower = League("5", "french ligue 1", "Soccer")
        assertEquals(listOf(lower), popularLeaguesOf(listOf(lower)))
    }

    @Test
    fun `offers nothing before the leagues are loaded`() {
        assertEquals(emptyList<League>(), LeagueUiState().popularLeagues)
    }
}
