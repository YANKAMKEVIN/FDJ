package com.kev.domain.repository

import com.kev.domain.model.League
import com.kev.domain.model.Team
import com.kev.domain.util.Result

/**
 * Repository contract for leagues and teams.
 * Keeps domain free of networking and persistence details.
 */
interface LeagueRepository {

    /**
     * Returns all leagues, possibly served from cache (Room) with a freshness policy.
     */
    suspend fun getAllLeagues(): Result<List<League>>

    /**
     * Fetches and stores teams for a given league name, then returns a snapshot list.
     * Business rules (anti-lexicographic + one-out-of-two) are guaranteed by persistence.
     */
    suspend fun getTeamsByLeagueName(leagueName: String): Result<List<Team>>

    /**
     * Forces a refresh of leagues from network and updates the cache.
     */
    suspend fun refreshLeagues(): Result<List<League>>

}
