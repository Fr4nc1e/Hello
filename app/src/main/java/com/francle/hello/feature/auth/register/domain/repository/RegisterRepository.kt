package com.francle.hello.feature.auth.register.domain.repository

import com.francle.hello.feature.auth.splash.data.response.AuthResult

interface RegisterRepository {
    suspend fun register(
        email: String,
        username: String,
        hashTag: String,
        password: String
    ): AuthResult<String>
}
