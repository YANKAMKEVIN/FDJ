package com.kev.data.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.kev.data.datasource.local.AppDatabase
import com.kev.data.datasource.local.dao.LeagueDao
import com.kev.data.datasource.local.entity.LeagueEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LeagueDaoTest {
    private lateinit var db: AppDatabase;
    private lateinit var dao: LeagueDao

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), AppDatabase::class.java
        ).allowMainThreadQueries().build(); dao = db.leagueDao()
    }

    @After
    fun tearDown() = db.close()

    @Test
    fun insert_and_getAll() = runTest {
        val leagues = (1..3).map { LeagueEntity("$it", "L$it", "Soccer") }
        dao.insertAll(leagues)
        val all = dao.getAll()
        assert(all.size == 3)
    }

    @Test
    fun search_is_case_insensitive_and_capped_to_10() = runTest {
        val leagues = (1..20).map { LeagueEntity("$it", "Premier L$it", "Soccer") }
        dao.insertAll(leagues)
        val results = dao.search("premier") // Flow<List<LeagueEntity>>
        val first = results.first()
        assert(first.size == 10)
    }
}