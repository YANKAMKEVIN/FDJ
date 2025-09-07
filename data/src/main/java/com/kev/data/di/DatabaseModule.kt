package com.kev.data.di

import android.content.Context
import androidx.room.Room
import com.kev.data.datasource.local.AppDatabase
import com.kev.data.datasource.local.dao.CacheMetaDao
import com.kev.data.datasource.local.dao.LeagueDao
import com.kev.data.datasource.local.dao.TeamDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDb(@ApplicationContext ctx: Context): AppDatabase =
        Room.databaseBuilder(ctx, AppDatabase::class.java, "parissportifs.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideLeagueDao(db: AppDatabase): LeagueDao = db.leagueDao()

    @Provides
    fun provideTeamDao(db: AppDatabase): TeamDao = db.teamDao()

    @Provides
    fun provideCacheMetaDao(db: AppDatabase): CacheMetaDao = db.cacheMetaDao()
}
