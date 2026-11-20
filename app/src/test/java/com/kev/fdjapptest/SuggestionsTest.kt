package com.kev.fdjapptest

import com.kev.domain.model.League
import com.kev.fdjapptest.ui.components.matchRange
import com.kev.fdjapptest.ui.home.LeagueUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SuggestionsTest {

    private val l1 = League("1", "French Ligue 1", "Soccer")

    @Test
    fun `matchRange finds the typed part case-insensitively`() {
        assertEquals(7..11, matchRange("French Ligue 1", "LIGUE"))
    }

    @Test
    fun `matchRange ignores surrounding spaces and returns null without match`() {
        assertEquals(0..5, matchRange("French Ligue 1", " french "))
        assertNull(matchRange("French Ligue 1", "nba"))
        assertNull(matchRange("French Ligue 1", "  "))
    }

    @Test
    fun `no-match row shows only when leagues are loaded and nothing matches`() {
        val typed = LeagueUiState(query = "xyz", leagues = listOf(l1), isSuggestionsOpen = true)
        assertTrue(typed.showNoSuggestionMatch)
        assertFalse(typed.copy(suggestions = listOf(l1)).showNoSuggestionMatch)
        assertFalse(typed.copy(isSuggestionsOpen = false).showNoSuggestionMatch)
        assertFalse(typed.copy(query = " ").showNoSuggestionMatch)
        // Leagues not loaded: the leagues error screen explains it, not the menu.
        assertFalse(typed.copy(leagues = emptyList()).showNoSuggestionMatch)
    }
}
