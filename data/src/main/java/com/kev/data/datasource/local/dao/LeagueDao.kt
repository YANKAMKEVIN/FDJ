package com.kev.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kev.data.datasource.local.entity.LeagueEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for leagues.
 */
@Dao
interface LeagueDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(leagues: List<LeagueEntity>)

    @Query("SELECT * FROM leagues")
    suspend fun getAll(): List<LeagueEntity>

    @Query("SELECT * FROM leagues WHERE name LIKE '%' || :q || '%' COLLATE NOCASE LIMIT 10")
    fun search(q: String): Flow<List<LeagueEntity>>

    @Query("SELECT COUNT(*) FROM leagues")
    suspend fun count(): Int
}
