package com.kev.data.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.kev.data.datasource.local.AppDatabase
import com.kev.data.datasource.local.dao.CacheMetaDao
import com.kev.data.datasource.local.dao.LeagueDao
import com.kev.data.datasource.local.dao.TeamDao
import com.kev.data.datasource.local.entity.TeamEntity
import com.kev.data.datasource.remote.api.TheSportsDbApi
import com.kev.domain.repository.LeagueRepository
import com.kev.domain.util.Result
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.atomic.AtomicLong

@RunWith(RobolectricTestRunner::class)
class LeagueRepositoryImplTest {

    private lateinit var db: AppDatabase
    private lateinit var leagueDao: LeagueDao
    private lateinit var teamDao: TeamDao
    private lateinit var cacheMetaDao: CacheMetaDao

    private lateinit var server: MockWebServer
    private lateinit var api: TheSportsDbApi
    private lateinit var repo: LeagueRepository

    // Controlled clock for TTL checks
    private val now = AtomicLong(1_000_000L)
    private val nowProvider: () -> Long = { now.get() }

    @Before
    fun setup() {
        // In-memory Room DB (no disk I/O)
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        //DAOs
        leagueDao = db.leagueDao()
        teamDao = db.teamDao()
        cacheMetaDao = db.cacheMetaDao()

        //MockWebServer +  Retrofit (Moshi)
        server = MockWebServer().apply { start() }
        val retrofit = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(MoshiConverterFactory.create())
            .client(OkHttpClient.Builder().build())
            .build()
        api = retrofit.create(TheSportsDbApi::class.java)

        //System under test
        repo = LeagueRepositoryImpl(
            api = api,
            leagueDao = leagueDao,
            teamDao = teamDao,
            cacheMetaDao = cacheMetaDao,
            db = db,
            nowProvider = nowProvider
        )
    }

    @After
    fun tearDown() {
        server.shutdown()
        db.close()
    }

    @Test
    fun `getTeamsByLeagueName - network OK - persists  all, returns every-other`() = runTest {

        server.enqueue(
            MockResponse().setResponseCode(200).setBody(
                """
                {"teams":[
                  {"idTeam":"1","strTeam":"Chelsea","strBadge":"u"},
                  {"idTeam":"2","strTeam":"Arsenal","strBadge":"v"},
                  {"idTeam":"3","strTeam":"Bournemouth","strBadge":"w"}
                ]}
                """.trimIndent()
            )
        )

        val res = repo.getTeamsByLeagueName("English Premier League")

        Assert.assertTrue(res is Result.Success)
        val teams = (res as Result.Success).value

        // Anti-lexicographic sort (desc) → "Chelsea","Bournemouth","Arsenal"
        // Keep every other (ordinal % 2 == 0) → indexes 0 and 2 → "Chelsea","Arsenal"
        Assert.assertEquals(listOf("Chelsea", "Arsenal"), teams.map { it.name })

        // DB should contain all inserted rows (3)
        val allPersisted = teamDao.dumpForLeague("English Premier League")
        Assert.assertEquals(3, allPersisted.size)
    }

    @Test
    fun `getTeamsByLeagueName - network fails - returns cached every-other`() =
        runTest {
            //Pre-seed cache for L1
            teamDao.upsertAll(
                listOf(
                    TeamEntity("10", "L1", "PSG", null, "psg", 0),
                    TeamEntity("20", "L1", "Lyon", null, "lyon", 1)
                )
            )

            // Simulate API error
            server.enqueue(MockResponse().setResponseCode(500))

            val res = repo.getTeamsByLeagueName("L1")
            Assert.assertTrue(res is Result.Success)
            val names = (res as Result.Success).value.map { it.name }

            // Fallback reads listForLeague (already filtered 1-of-2) → only "PSG"
            Assert.assertEquals(listOf("PSG"), names)
        }

    @Test
    fun `getTeamsByLeagueName - network fails and cache empty - returns Failure`() = runTest {
        server.enqueue(MockResponse().setResponseCode(500))

        val res = repo.getTeamsByLeagueName("Empty League")
        Assert.assertTrue(res is Result.Failure)
    }

    @Test
    fun `getAllLeagues - TTL respected - hits network only when stale`() = runTest {
        //first network response : 2 leagues
        server.enqueue(
            MockResponse().setResponseCode(200).setBody(
                """
                {"leagues":[
                  {"idLeague":"100","strLeague":"English Premier League","strSport":"Soccer"},
                  {"idLeague":"200","strLeague":"French Ligue 1","strSport":"Soccer"}
                ]}
                """.trimIndent()
            )
        )

        // 1) No meta → stale → fetch + seed
        val first = repo.getAllLeagues()
        Assert.assertTrue(first is Result.Success)
        Assert.assertEquals(
            listOf("English Premier League", "French Ligue 1"),
            (first as Result.Success).value.map { it.name })

        // 2) Do NOT enqueue a new response
        // Move time forward but keep it < TTL → should SERVE from DB, no network call
        val beforeTtl = now.get() + (24 * 60 * 60 * 1000L - 1) // TTL-1ms
        now.set(beforeTtl)

        val second = repo.getAllLeagues()
        Assert.assertTrue(second is Result.Success)
        Assert.assertEquals(1, server.requestCount) // une seule requête réseau effectuée

        // 3) Now exceed TTL → enqueue a new response and expect a re-fetch
        server.enqueue(
            MockResponse().setResponseCode(200).setBody(
                """
                {"leagues":[
                  {"idLeague":"100","strLeague":"English Premier League","strSport":"Soccer"}
                ]}
                """.trimIndent()
            )
        )
        now.set(beforeTtl + 2)

        val third = repo.getAllLeagues()
        Assert.assertTrue(third is Result.Success)
        Assert.assertEquals(2, server.requestCount)
    }

    @Test
    fun malformed_dtos_are_ignored_and_ordinals_assigned() = runTest {
        server.enqueue(
            MockResponse().setBody(
                """{"teams":[{"idTeam":null,"strTeam":"X","strBadge":"u"},
                 {"idTeam":"2","strTeam":"B","strBadge":"v"},
                 {"idTeam":"3","strTeam":"A","strBadge":"w"}]}"""
            )
        )
        val res = repo.getTeamsByLeagueName("L")
        require(res is Result.Success)
        // kept B,A → ordinals 0,1 → every-other returns only "B"
        Assert.assertEquals(listOf("B"), res.value.map { it.name })
        val dump = teamDao.dumpForLeague("L")
        Assert.assertEquals(listOf(0, 1), dump.map { it.ordinal })
    }

    @Test fun refreshLeagues_fetches_and_updates_cache_meta() = runTest {
        server.enqueue(MockResponse().setBody("""{"leagues":[{"idLeague":"1","strLeague":"X","strSport":"Soccer"}]}"""))
        val res = repo.refreshLeagues()
        assert(res is Result.Success && (res as Result.Success).value.first().name == "X")
        assert(cacheMetaDao.get("leagues") != null)
    }

}