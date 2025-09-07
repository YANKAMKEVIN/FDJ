package com.kev.data.api

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.kev.data.datasource.remote.api.TheSportsDbApi
import kotlinx.coroutines.runBlocking
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

@RunWith(RobolectricTestRunner::class)
class TheSportsDbApiContractTest {

    private lateinit var server: MockWebServer
    private lateinit var api: TheSportsDbApi

    @Before
    fun setUp() {
        // Start a local HTTP server
        server = MockWebServer().apply { start() }

        // Build Retrofit against the mock server
        val retrofit = Retrofit.Builder()
            .baseUrl(server.url("/")) // base is the mock server
            .addConverterFactory(MoshiConverterFactory.create())
            .client(OkHttpClient.Builder().build())
            .build()

        api = retrofit.create(TheSportsDbApi::class.java)

        // Ensure Robolectric bootstraps the context (optional; forces class loading)
        ApplicationProvider.getApplicationContext<Application>()
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `getAllLeagues hits expected path`() {
        // Enqueue a minimal valid body for the converter
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("""{"leagues":[]}""")
        )

        // Execute API call
        runBlockingUnit {
            api.getAllLeagues()
        }

        // Inspect the recorded HTTP request
        val request = server.takeRequest()
        Assert.assertEquals("GET", request.method)
        Assert.assertTrue(
            "Path should contain /api/v1/json/all_leagues.php, was: ${request.path}",
            request.path!!.contains("/api/v1/json/all_leagues.php")
        )
        // No query expected on this endpoint
        Assert.assertEquals(null, request.requestUrl!!.query)
    }

    @Test
    fun `getTeamsForLeague hits expected path with league query param`() {
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("""{"teams":[]}""")
        )

        val league = "French Ligue 1"

        runBlockingUnit {
            api.getTeamsForLeague(league)
        }

        val request = server.takeRequest()
        Assert.assertEquals("GET", request.method)
        Assert.assertTrue(
            "Path should contain /api/v1/json/search_all_teams.php, was: ${request.path}",
            request.path!!.contains("/api/v1/json/search_all_teams.php")
        )
        // The 'l' query parameter should be present and decoded as-is
        Assert.assertEquals(league, request.requestUrl!!.queryParameter("l"))
    }

    // --- small helper to avoid bringing kotlinx-coroutines-test here ---
    private fun runBlockingUnit(block: suspend () -> Unit) {
        runBlocking { block() }
    }
}