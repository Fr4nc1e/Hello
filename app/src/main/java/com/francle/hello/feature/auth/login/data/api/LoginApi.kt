package com.francle.hello.feature.auth.login.data.api

import com.francle.hello.feature.auth.login.data.request.LoginRequest
import com.francle.hello.feature.auth.login.data.response.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApi {
    @POST("api/user/login")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse
}
