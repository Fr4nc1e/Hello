package com.francle.hello.feature.auth.splash.di

import android.content.SharedPreferences
import com.francle.hello.feature.auth.splash.data.api.AuthApi
import com.francle.hello.feature.auth.splash.data.repository.AuthRepositoryImpl
import com.francle.hello.feature.auth.splash.domain.repository.AuthRepository
import com.francle.hello.core.util.Constants
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
object AuthModule {
    @Provides
    @Singleton
    fun provideAuthApi(client: OkHttpClient): AuthApi {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        api: AuthApi,
        preferences: SharedPreferences
    ): AuthRepository {
        return AuthRepositoryImpl(api = api, pref = preferences)
    }
}
