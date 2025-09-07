package com.kev.data.datasource.remote.api

import com.kev.data.datasource.remote.dto.AllLeaguesResponse
import com.kev.data.datasource.remote.dto.TeamsResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit service for interacting with TheSportsDB API.
 *
 * ⚠️ Note:
 * - Endpoints require an API key injected into the URL via an Interceptor
 *   (e.g., /api/v1/json/{APIKEY}/...).
 * - Paths are centralized in the [Endpoints] companion object.
 */
interface TheSportsDbApi {

    /**
     * Fetch all leagues available in TheSportsDB.
     *
     * Example: GET /api/v1/json/{APIKEY}/all_leagues.php
     *
     * @return [AllLeaguesResponse] containing the list of leagues (field `leagues`).
     */
    @GET(Endpoints.ALL_LEAGUES)
    suspend fun getAllLeagues(): AllLeaguesResponse

    /**
     * Fetch all teams for a given league, identified by its exact name.
     *
     * Example: GET /api/v1/json/{APIKEY}/search_all_teams.php?l=French%20Ligue%201
     *
     * @param leagueName exact league name (e.g., "French Ligue 1").
     * @return [TeamsResponse] containing the list of teams (field `teams`).
     */
    @GET(Endpoints.SEARCH_ALL_TEAMS)
    suspend fun getTeamsForLeague(
        @Query("l") leagueName: String
    ): TeamsResponse

    companion object Endpoints {
        /** Path for fetching all leagues */
        const val ALL_LEAGUES = "/api/v1/json/all_leagues.php"

        /** Path for fetching teams of a league */
        const val SEARCH_ALL_TEAMS = "/api/v1/json/search_all_teams.php"
    }
}