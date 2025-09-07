package com.kev.data.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.kev.data.datasource.local.AppDatabase
import com.kev.data.datasource.local.dao.CacheMetaDao
import com.kev.data.datasource.local.entity.CacheMetaEntity
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CacheMetaDaoTest {
    private lateinit var db: AppDatabase
    private lateinit var dao: CacheMetaDao

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), AppDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = db.cacheMetaDao()
    }

    @Test
    fun upsert_overwrites() = runTest {
        dao.upsert(CacheMetaEntity("leagues", 100L))
        dao.upsert(CacheMetaEntity("leagues", 200L))
        val meta = dao.get("leagues")
        assert(meta?.updatedAtMillis == 200L)
    }
}