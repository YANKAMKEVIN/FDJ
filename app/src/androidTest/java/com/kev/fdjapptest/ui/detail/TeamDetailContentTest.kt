package com.kev.fdjapptest.ui.detail

import androidx.annotation.StringRes
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.kev.domain.model.Team
import com.kev.domain.model.TeamDetails
import com.kev.domain.model.TeamLink
import com.kev.domain.util.DomainError
import com.kev.fdjapptest.R
import com.kev.fdjapptest.ui.theme.FDJAPPTESTTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Compose UI tests of the team detail screen, driven by a fixed [TeamDetailUiState].
 * Instrumented: run with `./gradlew connectedDebugAndroidTest` on a device or emulator.
 */
@RunWith(AndroidJUnit4::class)
class TeamDetailContentTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val arsenal = Team("133604", "Arsenal", null)

    private fun string(@StringRes id: Int): String =
        InstrumentationRegistry.getInstrumentation().targetContext.getString(id)

    private fun setContent(state: TeamDetailUiState, onRetry: () -> Unit = {}) {
        composeRule.setContent {
            FDJAPPTESTTheme {
                TeamDetailContent(state = state, onBack = {}, onRetry = onRetry, language = "en")
            }
        }
    }

    @Test
    fun loadedDetailsShowStadiumYearDescriptionAndLinks() {
        setContent(
            TeamDetailUiState(
                team = arsenal,
                details = TeamDetails(
                    id = "133604", name = "Arsenal", badgeUrl = null, stadium = "Emirates Stadium",
                    location = null, formedYear = 1886, descriptionEn = "Arsenal Football Club.",
                    descriptionFr = null,
                    links = listOf(TeamLink(TeamLink.Kind.Instagram, "https://instagram.com/arsenal"))
                )
            )
        )

        composeRule.onNodeWithText("Emirates Stadium").assertIsDisplayed()
        composeRule.onNodeWithText("1886").assertIsDisplayed()
        composeRule.onNodeWithText("Arsenal Football Club.").assertIsDisplayed()
        composeRule.onNodeWithText(string(R.string.team_detail_link_instagram)).assertIsDisplayed()
        composeRule.onNodeWithText(string(R.string.team_detail_link_facebook)).assertDoesNotExist()
    }

    @Test
    fun aTeamWithoutInformationSaysSo() {
        setContent(
            TeamDetailUiState(
                team = arsenal,
                details = TeamDetails(
                    id = "133604", name = "Arsenal", badgeUrl = null, stadium = null, location = null,
                    formedYear = null, descriptionEn = null, descriptionFr = null, links = emptyList()
                )
            )
        )

        composeRule.onNodeWithText(string(R.string.team_detail_no_information)).assertIsDisplayed()
        composeRule.onNodeWithText(string(R.string.team_detail_stadium)).assertDoesNotExist()
    }

    @Test
    fun theErrorOffersARetry() {
        var retries = 0
        setContent(TeamDetailUiState(team = arsenal, error = DomainError.Network), onRetry = { retries++ })

        composeRule.onNodeWithText(string(R.string.team_detail_error_network)).assertIsDisplayed()
        composeRule.onNodeWithText(string(R.string.retry_button_label)).performClick()
        assertEquals(1, retries)
    }
}
