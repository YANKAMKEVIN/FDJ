package com.kev.data.datasource.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Local league entity.
 *
 * Indexed by name for fast LIKE queries in autocomplete.
 */
@Entity(
    tableName = "leagues",
    indices = [Index(value = ["name"], unique = false)]
)
data class LeagueEntity(
    @PrimaryKey val id: String,
    val name: String,
    val sport: String?
)
