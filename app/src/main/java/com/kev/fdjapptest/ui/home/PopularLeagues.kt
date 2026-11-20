package com.kev.fdjapptest.ui.home

import com.kev.domain.model.League

/** Names (as returned by TheSportsDB) of the leagues offered in one tap on the start screen. */
internal val POPULAR_LEAGUE_NAMES = listOf(
    "English Premier League",
    "French Ligue 1",
    "Spanish La Liga",
    "German Bundesliga",
    "Italian Serie A",
    "NBA",
)

/**
 * The popular leagues present in [leagues], in the order of [POPULAR_LEAGUE_NAMES].
 * Only loaded leagues are offered, so a tap always selects a league the app knows.
 */
fun popularLeaguesOf(leagues: List<League>): List<League> {
    val byName = leagues.associateBy { it.name.lowercase() }
    return POPULAR_LEAGUE_NAMES.mapNotNull { byName[it.lowercase()] }
}
