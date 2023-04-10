package com.francle.hello.feature.login.domain.repository

import com.francle.hello.core.data.util.call.AuthResult

interface LoginRepository {
    suspend fun login(email: String, password: String): AuthResult<String>
}
