package com.kev.data.datasource.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * API response of `lookupteam.php?id=...`: a list holding the team, or `null` for an unknown id.
 *
 * Field names follow TheSportsDB v1 public documentation; NOT verified against the live API
 * (thesportsdb.com was unreachable when this was written). Every field is nullable and the
 * API sends "" or null for missing values.
 */
@JsonClass(generateAdapter = true)
data class TeamDetailsResponse(
    @Json(name = "teams") val teams: List<TeamDetailsDto>?
)

@JsonClass(generateAdapter = true)
data class TeamDetailsDto(
    @Json(name = "idTeam") val idTeam: String?,
    @Json(name = "strTeam") val teamName: String?,
    @Json(name = "strBadge") val badgeUrl: String?,
    @Json(name = "strStadium") val stadium: String?,
    @Json(name = "strLocation") val location: String?,
    /** A year sent as a string, e.g. "1886"; "" or "0" when unknown. */
    @Json(name = "intFormedYear") val formedYear: String?,
    @Json(name = "strDescriptionEN") val descriptionEn: String?,
    @Json(name = "strDescriptionFR") val descriptionFr: String?,
    /** Links usually come without scheme, e.g. "www.arsenal.com". */
    @Json(name = "strWebsite") val website: String?,
    @Json(name = "strFacebook") val facebook: String?,
    @Json(name = "strTwitter") val twitter: String?,
    @Json(name = "strInstagram") val instagram: String?,
    @Json(name = "strYoutube") val youtube: String?
)
