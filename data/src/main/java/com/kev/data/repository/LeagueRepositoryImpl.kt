package com.kev.data.repository

import androidx.room.withTransaction
import com.kev.data.datasource.local.AppDatabase
import com.kev.data.datasource.local.dao.CacheMetaDao
import com.kev.data.datasource.local.dao.LeagueDao
import com.kev.data.datasource.local.dao.TeamDao
import com.kev.data.datasource.local.entity.CacheMetaEntity
import com.kev.data.datasource.local.entity.LeagueEntity
import com.kev.data.datasource.local.entity.TeamEntity
import com.kev.data.datasource.remote.api.TheSportsDbApi
import com.kev.data.util.toDomainError
import com.kev.domain.model.League
import com.kev.domain.model.Team
import com.kev.domain.repository.LeagueRepository
import com.kev.domain.util.Result
import com.kev.domain.util.Result.Failure
import com.kev.domain.util.Result.Success
import javax.inject.Inject

/**
 * Repository implementation for leagues/teams.
 *
 * Strategy:
 * - Leagues: cache with TTL (Room + CacheMeta).
 * - Teams: on league selection, fetch remote once, compute deterministic order (anti-lexicographic),
 *   store into Room with precomputed `ordinal`, then serve via snapshot list.
 */
class LeagueRepositoryImpl @Inject constructor(
    private val api: TheSportsDbApi,
    private val leagueDao: LeagueDao,
    private val teamDao: TeamDao,
    private val cacheMetaDao: CacheMetaDao,
    private val db: AppDatabase,
    private val nowProvider: () -> Long = { System.currentTimeMillis() }
) : LeagueRepository {

    override suspend fun getAllLeagues(): Result<List<League>> = try {
        val meta = cacheMetaDao.get(LEAGUES_CACHE_KEY)
        val isStale = meta == null || (nowProvider() - meta.updatedAtMillis) > TTL_LEAGUES_MS

        if (isStale) {
            val remote = api.getAllLeagues().leagues.orEmpty()
            val entities = remote.map {
                LeagueEntity(
                    id = it.idLeague.orEmpty(),
                    name = it.leagueName.orEmpty(),
                    sport = it.sport
                )
            }
            // Seed Room + update cache meta in a transaction
            db.withTransaction {
                leagueDao.insertAll(entities)
                cacheMetaDao.upsert(CacheMetaEntity(LEAGUES_CACHE_KEY, nowProvider()))
            }
        }

        // Always serve from Room
        val local = leagueDao.getAll().map { League(it.id, it.name, it.sport) }
        Success(local)
    } catch (t: Throwable) {
        Failure(t.toDomainError(), t)
    }

    /**
     * Optional explicit refresh if you expose it in the domain interface.
     */
    override suspend fun refreshLeagues(): Result<List<League>> = try {
        val remote = api.getAllLeagues().leagues.orEmpty()
        val entities = remote.map {
            LeagueEntity(
                id = it.idLeague.orEmpty(),
                name = it.leagueName.orEmpty(),
                sport = it.sport
            )
        }
        db.withTransaction {
            leagueDao.insertAll(entities)
            cacheMetaDao.upsert(CacheMetaEntity(LEAGUES_CACHE_KEY, nowProvider()))
        }
        Success(leagueDao.getAll().map { League(it.id, it.name, it.sport) })
    } catch (t: Throwable) {
        Failure(t.toDomainError(), t)
    }

    // ===== Teams =====

    // data/repository/LeagueRepositoryImpl.kt
    override suspend fun getTeamsByLeagueName(leagueName: String): Result<List<Team>> = try {
        val remote = api.getTeamsForLeague(leagueName).teams.orEmpty()

        val prepared = remote.mapNotNull { dto ->
            val id = dto.idTeam?.trim()
            val name = dto.teamName?.trim()
            if (id.isNullOrEmpty() || name.isNullOrEmpty()) return@mapNotNull null
            TeamEntity(
                id = id,
                leagueName = leagueName,
                name = name,
                badgeUrl = dto.teamBadgeUrl,   // (strBadge dans ton DTO)
                orderKey = name.lowercase(),
                ordinal = 0
            )
        }
            .sortedByDescending { it.orderKey }
            .mapIndexed { index, e -> e.copy(ordinal = index) }

        db.withTransaction {
            teamDao.clearLeague(leagueName)
            teamDao.upsertAll(prepared)
        }

        Result.Success(
            prepared
                .filter { it.ordinal % 2 == 0 }           // règle “1 sur 2”
                .map { Team(it.id, it.name, it.badgeUrl) }
        )
    } catch (t: Throwable) {
        // Fallback local silencieux
        val cached = teamDao.listForLeague(leagueName)
        if (cached.isNotEmpty()) {
            Success(cached.map { Team(it.id, it.name, it.badgeUrl) })
        } else {
            Failure(t.toDomainError(), t)
        }
    }


    private companion object {
        const val LEAGUES_CACHE_KEY = "leagues"
        const val TTL_LEAGUES_MS = 24 * 60 * 60 * 1000L
    }
}
