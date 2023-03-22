package com.francle.hello.feature.register.domain.repository

import com.francle.hello.core.data.util.AuthResult

interface RegisterRepository {
    suspend fun register(email: String, username: String, password: String): AuthResult<String>
}
