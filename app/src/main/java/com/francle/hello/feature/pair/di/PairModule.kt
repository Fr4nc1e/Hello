package com.francle.hello.feature.pair.di

import com.francle.hello.core.util.Constants
import com.francle.hello.feature.pair.data.api.PairApi
import com.francle.hello.feature.pair.data.repository.PairRepositoryImpl
import com.francle.hello.feature.pair.domain.repository.PairRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PairModule {
    @Provides
    @Singleton
    fun providePairApi(client: OkHttpClient): PairApi {
        return Retrofit.Builder()
            .client(client)
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PairApi::class.java)
    }

    @Provides
    @Singleton
    fun providePairRepository(api: PairApi): PairRepository {
        return PairRepositoryImpl(api)
    }
}