package com.kev.data.mapper

import com.kev.data.datasource.remote.dto.TeamDetailsDto
import com.kev.domain.model.TeamDetails
import com.kev.domain.model.TeamLink

/**
 * Map a [TeamDetailsDto] (network) to [TeamDetails] (domain).
 *
 * Blank texts become null so the screen can hide them, the year is kept only when it is a
 * plausible year, and links get an https scheme when the API sends a bare host.
 */
fun TeamDetailsDto.toDomain(): TeamDetails = TeamDetails(
    id = idTeam.orEmpty().trim(),
    name = teamName.orEmpty().trim(),
    badgeUrl = badgeUrl.nullIfBlank(),
    stadium = stadium.nullIfBlank(),
    location = location.nullIfBlank(),
    formedYear = formedYear?.trim()?.toIntOrNull()?.takeIf { it in MIN_YEAR..MAX_YEAR },
    descriptionEn = descriptionEn.nullIfBlank(),
    descriptionFr = descriptionFr.nullIfBlank(),
    links = listOfNotNull(
        link(TeamLink.Kind.Website, website),
        link(TeamLink.Kind.Facebook, facebook),
        link(TeamLink.Kind.Twitter, twitter),
        link(TeamLink.Kind.Instagram, instagram),
        link(TeamLink.Kind.Youtube, youtube)
    )
)

private fun link(kind: TeamLink.Kind, raw: String?): TeamLink? =
    raw.toWebUrl()?.let { TeamLink(kind, it) }

/** "www.arsenal.com" → "https://www.arsenal.com"; an existing http(s) scheme is kept. */
internal fun String?.toWebUrl(): String? {
    val value = nullIfBlank() ?: return null
    val hasScheme = value.startsWith("http://", ignoreCase = true) ||
        value.startsWith("https://", ignoreCase = true)
    return if (hasScheme) value else "https://${value.removePrefix("//")}"
}

private fun String?.nullIfBlank(): String? = this?.trim()?.takeIf { it.isNotEmpty() }

private const val MIN_YEAR = 1800
private const val MAX_YEAR = 2100
