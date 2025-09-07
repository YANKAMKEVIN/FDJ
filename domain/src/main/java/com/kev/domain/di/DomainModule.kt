package com.kev.domain.di


import com.kev.domain.repository.LeagueRepository
import com.kev.domain.usecase.GetAllLeaguesUseCase
import com.kev.domain.usecase.GetTeamsForLeagueUseCase
import com.kev.domain.usecase.RefreshLeaguesUseCase
import com.kev.domain.util.DefaultDispatcherProvider
import com.kev.domain.util.DispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Provides domain use cases to the presentation layer.
 */
@Module
@InstallIn(SingletonComponent::class)
object DomainModule {

    @Provides
    @Singleton
    fun provideGetAllLeaguesUseCase(
        repo: LeagueRepository,
        dispatchers: DispatcherProvider
    ): GetAllLeaguesUseCase = GetAllLeaguesUseCase(repo, dispatchers)

    @Provides
    @Singleton
    fun provideGetTeamsForLeagueUseCase(
        repo: LeagueRepository,
        dispatchers: DispatcherProvider
    ): GetTeamsForLeagueUseCase = GetTeamsForLeagueUseCase(repo, dispatchers)

    @Provides
    @Singleton
    fun provideRefreshLeaguesUseCase(
        repo: LeagueRepository,
        dispatchers: DispatcherProvider
    ): RefreshLeaguesUseCase = RefreshLeaguesUseCase(repo, dispatchers)

    @Provides
    @Singleton
    fun provideDispatcherProvider(): DispatcherProvider = DefaultDispatcherProvider

}
