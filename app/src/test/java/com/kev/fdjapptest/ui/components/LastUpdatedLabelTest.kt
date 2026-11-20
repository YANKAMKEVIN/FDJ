package com.kev.fdjapptest.ui.components

import org.junit.Assert.assertEquals
import org.junit.Test

class LastUpdatedLabelTest {

    private val relative: (Long, Long) -> CharSequence = { t, now -> "${(now - t) / 60_000} min. ago" }

    private fun label(updatedAt: Long, now: Long) =
        lastUpdatedLabel(updatedAt, now, "Updated just now", "Updated %1\$s", relative)

    @Test
    fun `less than a minute reads just now`() {
        assertEquals("Updated just now", label(updatedAt = 1_000_000, now = 1_000_000 + 59_000))
    }

    @Test
    fun `a time in the future (clock change) reads just now`() {
        assertEquals("Updated just now", label(updatedAt = 2_000_000, now = 1_000_000))
    }

    @Test
    fun `older updates use the relative time`() {
        assertEquals("Updated 2 min. ago", label(updatedAt = 0, now = 120_000))
    }
}
