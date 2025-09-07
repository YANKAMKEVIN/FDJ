package com.kev.data.datasource.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * API response for all teams of a league.
 */
@JsonClass(generateAdapter = true)
data class TeamsResponse(
    @Json(name = "teams") val teams: List<TeamDto>?
)

/**
 * Data Transfer Object representing a team from TheSportsDB API.
 */
@JsonClass(generateAdapter = true)
data class TeamDto(
    @Json(name = "idTeam") val idTeam: String?,
    @Json(name = "strTeam") val teamName: String?,
    @Json(name = "strBadge") val teamBadgeUrl: String?
)
