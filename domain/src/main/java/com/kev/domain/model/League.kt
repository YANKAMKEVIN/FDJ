package com.kev.domain.model

/**
 * Domain model representing a sports league.
 *
 * This model is UI-agnostic and free from networking concerns.
 */
data class League(
    val id: String,
    val name: String,
    val sport: String?
)