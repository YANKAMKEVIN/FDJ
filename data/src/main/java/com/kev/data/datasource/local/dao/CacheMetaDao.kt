package com.kev.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kev.data.datasource.local.entity.CacheMetaEntity

/**
 * DAO for cache freshness metadata.
 */
@Dao
interface CacheMetaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(meta: CacheMetaEntity)

    @Query("SELECT * FROM cache_meta WHERE `key` = :key LIMIT 1")
    suspend fun get(key: String): CacheMetaEntity?

    // Optional helpers
    @Query("DELETE FROM cache_meta WHERE `key` = :key")
    suspend fun delete(key: String)
}
