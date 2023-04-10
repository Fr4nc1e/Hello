package com.francle.hello.feature.register.domain.repository

import com.francle.hello.core.data.util.call.AuthResult

interface RegisterRepository {
    suspend fun register(
        email: String,
        username: String,
        hashTag: String,
        password: String
    ): AuthResult<String>
}
