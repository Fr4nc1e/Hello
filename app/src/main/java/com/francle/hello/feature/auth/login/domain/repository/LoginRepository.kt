package com.francle.hello.feature.auth.login.domain.repository

import com.francle.hello.feature.auth.splash.data.response.AuthResult

interface LoginRepository {
    suspend fun login(email: String, password: String): AuthResult<String>
}
