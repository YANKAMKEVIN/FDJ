package com.kev.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kev.data.datasource.local.entity.TeamEntity

/**
 * DAO for teams.
 */
@Dao
interface TeamDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(teams: List<TeamEntity>)

    @Query("DELETE FROM teams WHERE leagueName = :leagueName")
    suspend fun clearLeague(leagueName: String)

    /**
     * Optional: a non-paged snapshot for small lists (useful in Free tier).
     */
    @Query(
        """
        SELECT * FROM teams
        WHERE leagueName = :leagueName AND (ordinal % 2) = 0
        ORDER BY ordinal ASC
    """
    )
    suspend fun listForLeague(leagueName: String): List<TeamEntity>

    //Debug functions
    @Query("SELECT COUNT(*) FROM teams WHERE leagueName = :leagueName")
    suspend fun countAllForLeague(leagueName: String): Int

    @Query("SELECT COUNT(*) FROM teams WHERE leagueName = :leagueName AND (ordinal % 2) = 0")
    suspend fun countEveryOtherForLeague(leagueName: String): Int

    @Query("SELECT * FROM teams WHERE leagueName = :leagueName ORDER BY ordinal ASC")
    suspend fun dumpForLeague(leagueName: String): List<TeamEntity>

}
