package com.kev.fdjapptest.ui.home

import com.kev.domain.model.League

/** A league offered in one tap on the start screen: its TheSportsDB id (`idLeague`) and name. */
internal data class PopularLeague(val id: String, val name: String)

/**
 * Leagues offered in one tap on the start screen, in display order.
 * Matched on the TheSportsDB id first, so a league renamed upstream is still offered; the name is the
 * fallback for a list whose ids differ (e.g. another data source).
 */
internal val POPULAR_LEAGUES = listOf(
    PopularLeague("4328", "English Premier League"),
    PopularLeague("4334", "French Ligue 1"),
    PopularLeague("4335", "Spanish La Liga"),
    PopularLeague("4331", "German Bundesliga"),
    PopularLeague("4332", "Italian Serie A"),
    PopularLeague("4387", "NBA"),
)

/**
 * The popular leagues present in [leagues], in the order of [POPULAR_LEAGUES].
 * Only loaded leagues are offered, so a tap always selects a league the app knows.
 */
fun popularLeaguesOf(leagues: List<League>): List<League> {
    val byId = leagues.associateBy { it.id }
    val byName = leagues.associateBy { it.name.lowercase() }
    return POPULAR_LEAGUES.mapNotNull { byId[it.id] ?: byName[it.name.lowercase()] }.distinct()
}
