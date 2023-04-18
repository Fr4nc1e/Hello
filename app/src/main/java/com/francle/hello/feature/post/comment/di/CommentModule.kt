package com.francle.hello.feature.post.comment.di

import android.app.Application
import com.francle.hello.core.util.Constants
import com.francle.hello.feature.post.comment.data.api.CommentApi
import com.francle.hello.feature.post.comment.data.repository.CommentRepositoryImpl
import com.francle.hello.feature.post.comment.domain.repository.CommentRepository
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
object CommentModule {
    @Provides
    @Singleton
    fun provideCommentApi(client: OkHttpClient): CommentApi {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(CommentApi::class.java)
    }

    @Provides
    @Singleton
    fun provideCommentRepository(
        api: CommentApi,
        app: Application,
        gson: Gson
    ): CommentRepository {
        return CommentRepositoryImpl(
            api = api,
            context = app,
            gson = gson
        )
    }
}
