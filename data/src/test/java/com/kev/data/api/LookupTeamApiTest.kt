package com.kev.data.api

import com.kev.data.datasource.remote.api.TheSportsDbApi
import com.kev.data.mapper.toDomain
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * Plain JVM test (no Robolectric) of `lookupteam.php`: path, query and parsing of a body shaped
 * like TheSportsDB documentation. The body is NOT a capture of the live API.
 */
class LookupTeamApiTest {

    private val server = MockWebServer().apply { start() }
    private val api = Retrofit.Builder()
        .baseUrl(server.url("/"))
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
        .create(TheSportsDbApi::class.java)

    @After
    fun tearDown() = server.shutdown()

    @Test
    fun `lookupTeam hits the expected path and parses the team`() = runTest {
        server.enqueue(
            MockResponse().setResponseCode(200).setBody(
                """
                {"teams":[{"idTeam":"133604","strTeam":"Arsenal","intFormedYear":"1886",
                "strStadium":"Emirates Stadium","strLocation":"Holloway, London",
                "strBadge":"https://r2.thesportsdb.com/images/media/team/badge/uyhbfe1612467038.png",
                "strWebsite":"www.arsenal.com","strFacebook":"www.facebook.com/Arsenal",
                "strTwitter":"twitter.com/arsenal","strInstagram":"instagram.com/arsenal",
                "strYoutube":"","strDescriptionEN":"Arsenal Football Club.","strDescriptionFR":null,
                "strSport":"Soccer","strLeague":"English Premier League"}]}
                """.trimIndent()
            )
        )

        val details = api.lookupTeam("133604").teams!!.single().toDomain()

        val request = server.takeRequest()
        assertEquals("/api/v1/json/lookupteam.php?id=133604", request.path)
        assertEquals("Emirates Stadium", details.stadium)
        assertEquals(1886, details.formedYear)
        assertEquals("Arsenal Football Club.", details.descriptionEn)
        assertNull(details.descriptionFr)
        assertEquals(4, details.links.size)
    }

    @Test
    fun `an unknown id parses as no team`() = runTest {
        server.enqueue(MockResponse().setResponseCode(200).setBody("""{"teams":null}"""))

        assertNull(api.lookupTeam("1").teams)
    }
}
