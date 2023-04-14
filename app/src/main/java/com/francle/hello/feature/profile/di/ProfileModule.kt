package com.francle.hello.feature.profile.di

import android.app.Application
import com.francle.hello.core.util.Constants
import com.francle.hello.feature.profile.data.api.ProfileApi
import com.francle.hello.feature.profile.data.repository.ProfileRepositoryImpl
import com.francle.hello.feature.profile.domain.repository.ProfileRepository
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
object ProfileModule {
    @Provides
    @Singleton
    fun provideProfileApi(client: OkHttpClient): ProfileApi {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ProfileApi::class.java)
    }

    @Provides
    @Singleton
    fun provideProfileRepository(
        api: ProfileApi,
        app: Application,
        gson: Gson
    ): ProfileRepository {
        return ProfileRepositoryImpl(
            profileApi = api,
            context = app,
            gson = gson
        )
    }
}
