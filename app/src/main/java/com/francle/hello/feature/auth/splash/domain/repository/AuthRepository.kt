package com.francle.hello.feature.auth.splash.domain.repository

import com.francle.hello.feature.auth.splash.data.response.AuthResult

interface AuthRepository {
    suspend fun authenticate(): AuthResult<Unit>
}
