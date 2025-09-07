package com.kev.data.mapper

import com.kev.data.datasource.remote.dto.LeagueDto
import com.kev.domain.model.League

/**
 * Map a [LeagueDto] (network) to a [League] (domain).
 *
 * - Uses safe defaults (`orEmpty`) to avoid null crashes at the edges.
 * - Trims string fields to prevent subtle UI issues (e.g., trailing spaces).
 * - Keeps optional fields as nullable to reflect real data incompleteness.
 */
fun LeagueDto.toDomain(): League = League(
    id = (idLeague ?: "").trim(),
    name = (leagueName ?: "").trim(),
    sport = sport?.trim()
)


/**
 * Convenience helpers for lists.
 */
fun List<LeagueDto>?.toDomainList(): List<League> =
    this.orEmpty().map { it.toDomain() }
