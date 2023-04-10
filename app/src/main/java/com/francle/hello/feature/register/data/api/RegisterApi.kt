package com.francle.hello.feature.register.data.api

import com.francle.hello.feature.register.data.request.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface RegisterApi {
    @POST("api/user/register")
    suspend fun register(
        @Body registerRequest: RegisterRequest
    )
}
