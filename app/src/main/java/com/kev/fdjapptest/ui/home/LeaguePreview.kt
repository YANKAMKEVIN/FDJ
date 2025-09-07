package com.kev.fdjapptest.ui.home

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.kev.domain.model.League
import com.kev.domain.model.Team
import com.kev.domain.util.Result

private fun sampleLeagues() = listOf(
    League("1", "French Ligue 1", "Soccer"),
    League("2", "English Premier League", "Soccer")
)

private fun sampleTeams() = listOf(
    Team("t1", "Paris SG", "https://via.placeholder.com/40"),
    Team("t2", "Marseille", "https://via.placeholder.com/40"),
    Team("t3", "Lyon", "https://via.placeholder.com/40")
)

@Preview(showBackground = true, name = "Success (snapshot)")
@Composable
fun LeagueScreen_Success_Preview() {
    val state = LeagueUiState(
        query = "French",
        leagues = sampleLeagues(),
        suggestions = listOf(sampleLeagues().first()),
        selectedLeague = sampleLeagues().first(),
        teams = Result.Success(sampleTeams()),
        isSuggestionsOpen = true,
        isRefreshing = false,
        lastUpdatedMillis = System.currentTimeMillis()
    )
    MaterialTheme {
        LeagueScreenContent(
            state = state,
            onQueryChange = {},
            onSelectLeague = {},
            onDismissSuggestions = {},
            onRefresh = {},
            onRetrySeed = {},
            onClearQuery = {},
            onDisplayModeChange = {}
        )
    }
}

@Preview(showBackground = true, name = "Empty teams")
@Composable
fun LeagueScreen_Empty_Preview() {
    val state = LeagueUiState(
        leagues = sampleLeagues(),
        selectedLeague = sampleLeagues().first(),
        teams = Result.Success(emptyList()),
        lastUpdatedMillis = System.currentTimeMillis()
    )
    MaterialTheme {
        LeagueScreenContent(
            state = state,
            onQueryChange = {},
            onSelectLeague = {},
            onDismissSuggestions = {},
            onRefresh = {},
            onRetrySeed = {},
            onClearQuery = {},
            onDisplayModeChange = {}
        )
    }
}

@Preview(showBackground = true, name = "Error state")
@Composable
fun LeagueScreen_Error_Preview() {
    val state = LeagueUiState(
        leagues = sampleLeagues(),
        selectedLeague = sampleLeagues().first(),
        teams = Result.Failure(error = com.kev.domain.util.DomainError.Network),
        lastUpdatedMillis = System.currentTimeMillis()
    )
    MaterialTheme {
        LeagueScreenContent(
            state = state,
            onQueryChange = {},
            onSelectLeague = {},
            onDismissSuggestions = {},
            onRefresh = {},
            onRetrySeed = {},
            onClearQuery = {},
            onDisplayModeChange = {}
        )
    }
}

@Preview(showBackground = true, name = "Paging")
@Composable
fun LeagueScreen_Paging_Preview() {
    val state = LeagueUiState(
        leagues = sampleLeagues(),
        selectedLeague = sampleLeagues().first(),
        teams = Result.Success(sampleTeams()),
        lastUpdatedMillis = System.currentTimeMillis()
    )

    MaterialTheme {
        LeagueScreenContent(
            state = state,
            onQueryChange = {},
            onSelectLeague = {},
            onDismissSuggestions = {},
            onRefresh = {},
            onRetrySeed = {},
            onClearQuery = {},
            onDisplayModeChange = {}
        )
    }
}
