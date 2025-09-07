package com.kev.data.datasource.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * API response for all leagues.
 */
@JsonClass(generateAdapter = true)
data class AllLeaguesResponse(
    @Json(name = "leagues") val leagues: List<LeagueDto>?
)

/**
 * Data Transfer Object representing a league from TheSportsDB API.
 */
@JsonClass(generateAdapter = true)
data class LeagueDto(
    @Json(name = "idLeague") val idLeague: String?,
    @Json(name = "strLeague") val leagueName: String?,
    @Json(name = "strSport") val sport: String?
)