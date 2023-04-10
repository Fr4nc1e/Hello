package com.francle.hello.feature.post.like.di

import com.francle.hello.core.util.Constants
import com.francle.hello.feature.post.like.data.api.LikeApi
import com.francle.hello.feature.post.like.data.repository.LikeRepositoryImpl
import com.francle.hello.feature.post.like.domain.repository.LikeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
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
