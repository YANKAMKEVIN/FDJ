package com.kev.fdjapptest.ui.home

import androidx.annotation.StringRes
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.kev.domain.model.League
import com.kev.domain.model.Team
import com.kev.domain.util.DomainError
import com.kev.domain.util.Result
import com.kev.fdjapptest.R
import com.kev.fdjapptest.ui.theme.FDJAPPTESTTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Compose UI tests of the league screen, driven by a fixed [LeagueUiState] (no ViewModel, no network).
 * Instrumented: run with `./gradlew connectedDebugAndroidTest` on a device or emulator.
 */
@RunWith(AndroidJUnit4::class)
class LeagueScreenContentTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val epl = League("4328", "English Premier League", "Soccer")
    private val l1 = League("4334", "French Ligue 1", "Soccer")

    private fun string(@StringRes id: Int): String =
        InstrumentationRegistry.getInstrumentation().targetContext.getString(id)

    private fun setContent(
        state: LeagueUiState,
        onSelectLeague: (League) -> Unit = {},
        onRetryLeagues: () -> Unit = {},
    ) {
        composeRule.setContent {
            FDJAPPTESTTheme {
                LeagueScreenContent(
                    state = state,
                    onQueryChange = {},
                    onSelectLeague = onSelectLeague,
                    onDismissSuggestions = {},
                    onRefresh = {},
                    onRetrySeed = {},
                    onClearQuery = {},
                    onDisplayModeChange = {},
                    onRetryLeagues = onRetryLeagues,
                )
            }
        }
    }

    @Test
    fun tappingAPopularLeagueSelectsIt() {
        val selected = mutableListOf<League>()
        setContent(LeagueUiState(leagues = listOf(epl, l1)), onSelectLeague = { selected += it })

        composeRule.onNodeWithText(string(R.string.popular_leagues_title)).assertIsDisplayed()
        composeRule.onNodeWithText(l1.name).performClick()

        assertEquals(listOf(l1), selected)
    }

    @Test
    fun leaguesErrorOffersRetry() {
        var retries = 0
        setContent(LeagueUiState(leaguesError = DomainError.Network), onRetryLeagues = { retries++ })

        composeRule.onNodeWithText(string(R.string.leagues_error_network_message)).assertIsDisplayed()
        composeRule.onNodeWithText(string(R.string.retry_button_label)).performClick()

        assertEquals(1, retries)
    }

    @Test
    fun selectedLeagueShowsItsTeamNames() {
        val teams = listOf(Team("1", "Paris SG", null), Team("2", "Lyon", null))
        setContent(
            LeagueUiState(
                query = l1.name,
                leagues = listOf(epl, l1),
                selectedLeague = l1,
                teams = Result.Success(teams),
            )
        )

        composeRule.onNodeWithText("Paris SG").assertIsDisplayed()
        composeRule.onNodeWithText("Lyon").assertIsDisplayed()
    }
}
