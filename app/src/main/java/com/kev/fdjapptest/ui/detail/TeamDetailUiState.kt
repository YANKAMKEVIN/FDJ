package com.kev.fdjapptest.ui.detail

import com.kev.domain.model.Team
import com.kev.domain.model.TeamDetails
import com.kev.domain.util.DomainError

/**
 * UI state of the team detail screen.
 *
 * [team] is the team tapped in the list: its name and badge are shown at once, while
 * [details] load.
 */
data class TeamDetailUiState(
    val team: Team? = null,
    val isLoading: Boolean = false,
    val details: TeamDetails? = null,
    val error: DomainError? = null
) {
    val name: String get() = details?.name?.takeIf { it.isNotBlank() } ?: team?.name.orEmpty()
    val badgeUrl: String? get() = details?.badgeUrl ?: team?.badgeUrl

    /** Loaded, but TheSportsDB has nothing to show besides the name: say so instead of a blank page. */
    val hasNoInformation: Boolean
        get() = details != null && details.stadium == null && details.formedYear == null &&
            details.descriptionEn == null && details.descriptionFr == null && details.links.isEmpty()
}
