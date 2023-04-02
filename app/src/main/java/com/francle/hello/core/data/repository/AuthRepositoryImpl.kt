package com.francle.hello.core.data.repository

import android.content.SharedPreferences
import com.francle.hello.core.data.api.AuthApi
import com.francle.hello.core.data.util.call.AuthResult
import com.francle.hello.core.domain.repository.AuthRepository
import com.francle.hello.core.util.Constants
import com.francle.hello.core.util.Constants.KEY_JWT_TOKEN
import retrofit2.HttpException

class AuthRepositoryImpl(
    private val api: AuthApi,
    private val pref: SharedPreferences
) : AuthRepository {
    override suspend fun authenticate(): AuthResult<Unit> {
        return try {
            val token = pref.getString(KEY_JWT_TOKEN, null) ?: return AuthResult.Unauthorized()
            val response = api.authenticate("Bearer $token")
            pref.edit().putString(Constants.PROFILE_IMAGE_URL, response.profileImageUrl).apply()
            AuthResult.Authorized()
        } catch (e: HttpException) {
            if (e.code() == 401) {
                AuthResult.Unauthorized()
            } else {
                AuthResult.UnknownError()
            }
        } catch (e: Exception) {
            AuthResult.UnknownError()
        }
    }
}
