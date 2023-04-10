package com.francle.hello.feature.register.di

import com.francle.hello.core.util.Constants
import com.francle.hello.feature.register.data.api.RegisterApi
import com.francle.hello.feature.register.data.repository.RegisterRepositoryImpl
import com.francle.hello.feature.register.domain.repository.RegisterRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object RegisterModule {
    @Provides
    @Singleton
    fun provideRegisterApi(): RegisterApi {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RegisterApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRegisterRepository(api: RegisterApi): RegisterRepository {
        return RegisterRepositoryImpl(api)
    }
}
