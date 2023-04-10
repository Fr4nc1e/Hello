package com.francle.hello.feature.like.di

import com.francle.hello.core.util.Constants
import com.francle.hello.feature.like.domain.repository.LikeRepository
import com.francle.hello.feature.like.data.api.LikeApi
import com.francle.hello.feature.like.data.repository.LikeRepositoryImpl
import dagger.Provides
import javax.inject.Singleton
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object LikeModule {
    @Provides
    @Singleton
    fun provideLikeApi(client: OkHttpClient): LikeApi {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LikeApi::class.java)
    }

    @Provides
    @Singleton
    fun provideLikeRepository(api: LikeApi): LikeRepository {
        return LikeRepositoryImpl(api)
    }
}
