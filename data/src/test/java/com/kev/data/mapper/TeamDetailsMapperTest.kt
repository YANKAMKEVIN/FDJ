package com.kev.data.mapper

import com.kev.data.datasource.remote.dto.TeamDetailsDto
import com.kev.domain.model.TeamLink
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Plain JVM tests (no Robolectric). */
class TeamDetailsMapperTest {

    private fun dto(
        stadium: String? = "Emirates Stadium",
        formedYear: String? = "1886",
        descriptionEn: String? = "Arsenal Football Club is a professional football club.",
        descriptionFr: String? = null,
        website: String? = "www.arsenal.com",
        facebook: String? = "www.facebook.com/Arsenal",
        twitter: String? = "twitter.com/arsenal",
        instagram: String? = "instagram.com/arsenal",
        youtube: String? = "www.youtube.com/user/ArsenalTour"
    ) = TeamDetailsDto(
        idTeam = " 133604 ", teamName = " Arsenal ",
        badgeUrl = "https://r2.thesportsdb.com/images/media/team/badge/uyhbfe1612467038.png",
        stadium = stadium, location = "Holloway, London",
        formedYear = formedYear, descriptionEn = descriptionEn, descriptionFr = descriptionFr,
        website = website, facebook = facebook, twitter = twitter,
        instagram = instagram, youtube = youtube
    )

    @Test
    fun `maps a complete team`() {
        val details = dto().toDomain()

        assertEquals("133604", details.id)
        assertEquals("Arsenal", details.name)
        assertEquals("Emirates Stadium", details.stadium)
        assertEquals("Holloway, London", details.location)
        assertEquals(1886, details.formedYear)
        assertEquals("Arsenal Football Club is a professional football club.", details.descriptionEn)
        assertEquals(
            listOf(
                TeamLink(TeamLink.Kind.Website, "https://www.arsenal.com"),
                TeamLink(TeamLink.Kind.Facebook, "https://www.facebook.com/Arsenal"),
                TeamLink(TeamLink.Kind.Twitter, "https://twitter.com/arsenal"),
                TeamLink(TeamLink.Kind.Instagram, "https://instagram.com/arsenal"),
                TeamLink(TeamLink.Kind.Youtube, "https://www.youtube.com/user/ArsenalTour")
            ),
            details.links
        )
    }

    @Test
    fun `blank and missing fields become null and empty links are dropped`() {
        val details = dto(
            stadium = "  ", formedYear = "", descriptionEn = "", descriptionFr = null,
            website = "", facebook = null, twitter = " ", instagram = null, youtube = ""
        ).toDomain()

        assertNull(details.stadium)
        assertNull(details.formedYear)
        assertNull(details.descriptionEn)
        assertNull(details.descriptionFr)
        assertTrue(details.links.isEmpty())
    }

    @Test
    fun `an implausible year is dropped`() {
        assertNull(dto(formedYear = "0").toDomain().formedYear)
        assertNull(dto(formedYear = "n/a").toDomain().formedYear)
        assertEquals(1902, dto(formedYear = " 1902 ").toDomain().formedYear)
    }

    @Test
    fun `an existing scheme is kept`() {
        assertEquals("http://example.org", "http://example.org".toWebUrl())
        assertEquals("HTTPS://example.org", "HTTPS://example.org".toWebUrl())
        assertEquals("https://example.org/x", "//example.org/x".toWebUrl())
        assertNull("   ".toWebUrl())
        assertNull(null.toWebUrl())
    }
}
