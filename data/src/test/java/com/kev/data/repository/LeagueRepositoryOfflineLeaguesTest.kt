package com.kev.data.repository

import com.kev.data.datasource.local.AppDatabase
import com.kev.data.datasource.local.dao.CacheMetaDao
import com.kev.data.datasource.local.dao.LeagueDao
import com.kev.data.datasource.local.dao.TeamDao
import com.kev.data.datasource.local.entity.CacheMetaEntity
import com.kev.data.datasource.local.entity.LeagueEntity
import com.kev.data.datasource.remote.api.TheSportsDbApi
import com.kev.domain.model.League
import com.kev.domain.util.DomainError
import com.kev.domain.util.Result
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.IOException

/**
 * Plain JVM tests (no Robolectric): the network fails before any Room transaction is opened,
 * so the DAOs can be mocked.
 */
class LeagueRepositoryOfflineLeaguesTest {

    private val api = mockk<TheSportsDbApi>()
    private val leagueDao = mockk<LeagueDao>()
    private val teamDao = mockk<TeamDao>()
    private val cacheMetaDao = mockk<CacheMetaDao>()
    private val db = mockk<AppDatabase>()

    private val now = 10 * DAY_MS
    private val repo = LeagueRepositoryImpl(api, leagueDao, teamDao, cacheMetaDao, db) { now }

    @Test
    fun `an expired cache is served when the network fails`() = runTest {
        coEvery { cacheMetaDao.get("leagues") } returns CacheMetaEntity("leagues", now - 2 * DAY_MS)
        coEvery { api.getAllLeagues() } throws IOException("offline")
        coEvery { leagueDao.count() } returns 1
        coEvery { leagueDao.getAll() } returns listOf(LeagueEntity("4334", "French Ligue 1", "Soccer"))

        val res = repo.getAllLeagues()

        assertEquals(Result.Success(listOf(League("4334", "French Ligue 1", "Soccer"))), res)
    }

    @Test
    fun `fails with a network error when nothing was ever cached`() = runTest {
        coEvery { cacheMetaDao.get("leagues") } returns null
        coEvery { api.getAllLeagues() } throws IOException("offline")
        coEvery { leagueDao.count() } returns 0

        val res = repo.getAllLeagues()

        assertEquals(DomainError.Network, (res as Result.Failure).error)
    }

    private companion object {
        const val DAY_MS = 24 * 60 * 60 * 1000L
    }
}
