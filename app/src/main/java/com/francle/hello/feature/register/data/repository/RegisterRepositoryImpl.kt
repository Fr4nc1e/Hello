package com.francle.hello.feature.register.data.repository

import com.francle.hello.core.data.util.call.AuthResult
import com.francle.hello.feature.register.data.api.RegisterApi
import com.francle.hello.feature.register.data.request.RegisterRequest
import com.francle.hello.feature.register.domain.repository.RegisterRepository
import retrofit2.HttpException

class RegisterRepositoryImpl(
    private val api: RegisterApi
) : RegisterRepository {
    override suspend fun register(
        email: String,
        username: String,
        hashTag: String,
        password: String
    ): AuthResult<String> {
        return try {
            api.register(
                RegisterRequest(
                    email = email,
                    username = username,
                    hashTag = hashTag,
                    password = password
                )
            )
            AuthResult.Authorized()
        } catch (e: HttpException) {
            if (e.code() == 409) {
                AuthResult.UnknownError("Email address already existed.")
            } else if (e.code() == 504) {
                AuthResult.UnknownError("Create account failed.")
            } else {
                AuthResult.UnknownError()
            }
        } catch (e: Exception) {
            AuthResult.UnknownError()
        }
    }
}
