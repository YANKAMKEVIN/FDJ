package com.kev.data.di

import com.kev.data.BuildConfig
import com.kev.data.datasource.remote.api.TheSportsDbApi
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://www.thesportsdb.com"

    /**
     * Interceptor that injects the API key segment into `/api/v1/json/...` paths.
     * Example: `/api/v1/json/all_leagues.php` -> `/api/v1/json/{APIKEY}/all_leagues.php`
     */
    @Provides
    @Singleton
    fun provideApiKeyInjector(): Interceptor = Interceptor { chain ->
        val original: Request = chain.request()
        val originalUrl = original.url
        val newUrl = originalUrl.newBuilder()
            .encodedPath(
                originalUrl.encodedPath.replace(
                    "/api/v1/json/",
                    "/api/v1/json/${BuildConfig.TSDB_API_KEY}/"
                )
            )
            .build()
        chain.proceed(original.newBuilder().url(newUrl).build())
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(apiKeyInjector: Interceptor): OkHttpClient {
        val builder = OkHttpClient.Builder().addInterceptor(apiKeyInjector)

        // Optional: log in debug builds
        val logger = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC
            else HttpLoggingInterceptor.Level.NONE
        }
        builder.addInterceptor(logger)

        return builder.build()
    }

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder().build()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient, moshi: Moshi): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    @Provides
    @Singleton
    fun provideTheSportsDbApi(retrofit: Retrofit): TheSportsDbApi =
        retrofit.create(TheSportsDbApi::class.java)
}
