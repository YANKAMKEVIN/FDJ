package com.kev.domain.model

/**
 * Details of one team, shown by the team detail screen.
 *
 * Every field but [id] and [name] may be missing in TheSportsDB: callers hide what is null.
 */
data class TeamDetails(
    val id: String,
    val name: String,
    val badgeUrl: String?,
    val stadium: String?,
    val location: String?,
    val formedYear: Int?,
    val descriptionEn: String?,
    val descriptionFr: String?,
    val links: List<TeamLink>
) {
    /**
     * Description in [language] (ISO 639 code, e.g. "fr"), falling back to the other language
     * so that a team described only in English still shows a description, and the reverse.
     */
    fun descriptionFor(language: String): String? =
        if (language.equals("fr", ignoreCase = true)) descriptionFr ?: descriptionEn
        else descriptionEn ?: descriptionFr
}

/** A web page of the team, opened in the browser. */
data class TeamLink(val kind: Kind, val url: String) {
    enum class Kind { Website, Facebook, Twitter, Instagram, Youtube }
}
