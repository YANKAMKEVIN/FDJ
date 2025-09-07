package com.kev.data.mapper

import com.kev.data.datasource.remote.dto.TeamDto
import com.kev.domain.model.Team

/**
 * Map a [TeamDto] (network) to a [Team] (domain).
 *
 * Notes:
 * - We intentionally keep `badgeUrl` nullable; callers can decide how to render placeholders.
 * - We do not enforce non-blank `name` here; filtering/validation is usually done higher up
 *   (e.g., repository or use case), depending on UX rules.
 */
fun TeamDto.toDomain(): Team = Team(
    id = (idTeam ?: "").trim(),
    name = (teamName ?: "").trim(),
    badgeUrl = teamBadgeUrl?.trim()
)

/**
 * Convenience helpers for lists.
 */
fun List<TeamDto>?.toDomainList(): List<Team> =
    this.orEmpty().map { it.toDomain() }