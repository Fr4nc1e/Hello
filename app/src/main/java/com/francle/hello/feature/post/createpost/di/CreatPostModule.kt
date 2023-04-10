package com.francle.hello.feature.post.createpost.di

import android.app.Application
import com.francle.hello.core.util.Constants
import com.francle.hello.feature.post.createpost.data.api.CreatePostApi
import com.francle.hello.feature.post.createpost.data.repository.CreatePostRepositoryImpl
import com.francle.hello.feature.post.createpost.domain.repository.CreatePostRepository
import com.google.gson.Gson
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
object CreatePostModule {
    @Provides
    @Singleton
    fun provideCreatePostApi(client: OkHttpClient): CreatePostApi {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CreatePostApi::class.java)
    }

    @Provides
    @Singleton
    fun provideCreatePostRepository(
        api: CreatePostApi,
        app: Application,
        gson: Gson
    ): CreatePostRepository {
        return CreatePostRepositoryImpl(
            api = api,
            context = app,
            gson = gson
        )
    }
}
