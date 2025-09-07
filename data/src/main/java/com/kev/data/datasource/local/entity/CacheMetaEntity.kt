package com.kev.data.datasource.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Cache metadata used to track freshness (TTL) per logical key.
 */
@Entity(tableName = "cache_meta", indices = [Index(value = ["key"], unique = true)])

data class CacheMetaEntity(
    @PrimaryKey val key: String,
    val updatedAtMillis: Long
)