package com.francle.hello.feature.auth.splash.data.api

import com.francle.hello.feature.auth.splash.data.response.UserInfoResponse
import retrofit2.http.GET
import retrofit2.http.Header

interface AuthApi {
    @GET("api/user/authenticate")
    suspend fun authenticate(
        @Header("Authorization") token: String
    ): UserInfoResponse
}
