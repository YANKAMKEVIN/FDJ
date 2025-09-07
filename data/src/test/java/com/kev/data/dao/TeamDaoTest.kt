package com.kev.data.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kev.data.datasource.local.AppDatabase
import com.kev.data.datasource.local.dao.TeamDao
import com.kev.data.datasource.local.entity.TeamEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TeamDaoTest {
    private lateinit var db: AppDatabase
    private lateinit var dao: TeamDao

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), AppDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = db.teamDao()
    }

    @After
    fun tearDown() = db.close()

    @Test
    fun everyOther_rule_is_applied() = runTest {
        val league = "French Ligue 1"
        val entities = listOf(
            TeamEntity("idA", league, "PSG", null, "psg", 0),
            TeamEntity("idB", league, "Lyon", null, "lyon", 1),
            TeamEntity("idC", league, "Nice", null, "nice", 2),
            TeamEntity("idD", league, "Metz", null, "metz", 3),
        )
        dao.upsertAll(entities)
        val half = dao.listForLeague(league)
        assert(half.map { it.id } == listOf("idA", "idC"))
    }
}