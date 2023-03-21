package com.francle.hello.feature.login.data.api

import com.francle.hello.feature.login.data.dto.TokenDto
import com.francle.hello.feature.login.data.request.AuthRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApi {
    @POST("api/user/login")
    suspend fun login(@Body authRequest: AuthRequest): TokenDto
}
