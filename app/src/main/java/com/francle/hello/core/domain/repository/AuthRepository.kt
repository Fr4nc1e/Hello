package com.francle.hello.core.domain.repository

import com.francle.hello.core.data.util.AuthResult

interface AuthRepository {
    suspend fun authenticate(): AuthResult<Unit>
}
