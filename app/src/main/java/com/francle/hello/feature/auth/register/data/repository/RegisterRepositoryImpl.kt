package com.francle.hello.feature.auth.register.data.repository

import com.francle.hello.feature.auth.splash.data.response.AuthResult
import com.francle.hello.feature.auth.register.data.api.RegisterApi
import com.francle.hello.feature.auth.register.data.request.RegisterRequest
import com.francle.hello.feature.auth.register.domain.repository.RegisterRepository
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
