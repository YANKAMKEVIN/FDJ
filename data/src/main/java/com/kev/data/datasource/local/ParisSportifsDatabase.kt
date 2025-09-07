package com.kev.data.datasource.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kev.data.datasource.local.dao.CacheMetaDao
import com.kev.data.datasource.local.dao.LeagueDao
import com.kev.data.datasource.local.dao.TeamDao
import com.kev.data.datasource.local.entity.CacheMetaEntity
import com.kev.data.datasource.local.entity.LeagueEntity
import com.kev.data.datasource.local.entity.TeamEntity

/**
 * Room database for the app.
 *
 * NOTE:
 * - Bumped to version = 2 to include CacheMetaEntity from the start.
 * - For a test exercise, you can enable destructive migration in the builder if you prefer.
 */
@Database(
    entities = [LeagueEntity::class, TeamEntity::class, CacheMetaEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun leagueDao(): LeagueDao
    abstract fun teamDao(): TeamDao
    abstract fun cacheMetaDao(): CacheMetaDao
}
