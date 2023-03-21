package com.francle.hello.core.data.api

import retrofit2.http.GET
import retrofit2.http.Header

interface AuthApi {
    @GET("api/user/authenticate")
    suspend fun authenticate(
        @Header("Authorization") token: String
    )
}
