package com.kev.data.di

import com.kev.data.datasource.local.AppDatabase
import com.kev.data.datasource.local.dao.CacheMetaDao
import com.kev.data.datasource.local.dao.LeagueDao
import com.kev.data.datasource.local.dao.TeamDao
import com.kev.data.datasource.remote.api.TheSportsDbApi
import com.kev.data.repository.LeagueRepositoryImpl
import com.kev.domain.repository.LeagueRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideLeagueRepository(
        api: TheSportsDbApi,
        leagueDao: LeagueDao,
        teamDao: TeamDao,
        cacheMetaDao: CacheMetaDao,
        db: AppDatabase,
    ): LeagueRepository = LeagueRepositoryImpl(
        api = api,
        leagueDao = leagueDao,
        teamDao = teamDao,
        cacheMetaDao = cacheMetaDao,
        db = db,
    )
}
