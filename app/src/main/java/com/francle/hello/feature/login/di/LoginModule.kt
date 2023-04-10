package com.francle.hello.feature.login.di

import android.content.SharedPreferences
import com.francle.hello.core.util.Constants
import com.francle.hello.feature.login.data.api.LoginApi
import com.francle.hello.feature.login.data.repository.LoginRepositoryImpl
import com.francle.hello.feature.login.domain.repository.LoginRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object LoginModule {
    @Provides
    @Singleton
    fun provideLoginApi(): LoginApi {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LoginApi::class.java)
    }

    @Provides
    @Singleton
    fun provideLoginRepository(
        api: LoginApi,
        preferences: SharedPreferences
    ): LoginRepository {
        return LoginRepositoryImpl(
            api = api,
            pref = preferences
        )
    }
}
