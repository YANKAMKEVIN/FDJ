package com.kev.data.datasource.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Local team entity.
 *
 * We keep:
 * - leagueName: functional key for fetching teams by league.
 * - orderKey: normalized team name for stable sorting (descending).
 * - ordinal: position after sorting; enables "one out of two".
 */
@Entity(
    tableName = "teams",
    indices = [
        Index(value = ["leagueName", "ordinal"], unique = false),
        Index(value = ["orderKey"], unique = false)
    ]
)
data class TeamEntity(
    @PrimaryKey val id: String,
    val leagueName: String,
    val name: String,
    val badgeUrl: String?,
    val orderKey: String,
    val ordinal: Int
)
