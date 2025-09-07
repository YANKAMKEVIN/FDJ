package com.kev.domain.usecase

import com.kev.domain.model.League
import com.kev.domain.repository.LeagueRepository
import com.kev.domain.util.DispatcherProvider
import com.kev.domain.util.Result
import kotlinx.coroutines.withContext

/**
 * Returns an immediate snapshot after seeding Room;
 */
class GetAllLeaguesUseCase(
    private val repo: LeagueRepository,
    private val dispatchers: DispatcherProvider
) {
    suspend operator fun invoke(): Result<List<League>> =
        withContext(dispatchers.io) { repo.getAllLeagues() }
}