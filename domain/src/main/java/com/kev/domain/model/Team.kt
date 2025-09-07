package com.kev.domain.model

/**
 * Domain model representing a team.
 *
 * Keep only what the app needs at the business/UI level.
 */
data class Team(
    val id: String,
    val name: String,
    val badgeUrl: String?
)