package com.kev.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TeamDetailsTest {

    private fun details(en: String?, fr: String?) = TeamDetails(
        id = "133604", name = "Arsenal", badgeUrl = null, stadium = null, location = null,
        formedYear = null, descriptionEn = en, descriptionFr = fr, links = emptyList()
    )

    @Test
    fun `French readers get the French description`() {
        assertEquals("FR", details(en = "EN", fr = "FR").descriptionFor("fr"))
    }

    @Test
    fun `other readers get the English description`() {
        assertEquals("EN", details(en = "EN", fr = "FR").descriptionFor("de"))
    }

    @Test
    fun `the other language is used when the preferred one is missing`() {
        assertEquals("EN", details(en = "EN", fr = null).descriptionFor("fr"))
        assertEquals("FR", details(en = null, fr = "FR").descriptionFor("en"))
    }

    @Test
    fun `no description at all gives null`() {
        assertNull(details(en = null, fr = null).descriptionFor("fr"))
    }
}
