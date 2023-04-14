package com.francle.hello.feature.auth.login.data.repository

import android.content.SharedPreferences
import com.francle.hello.core.util.Constants
import com.francle.hello.feature.auth.login.data.api.LoginApi
import com.francle.hello.feature.auth.login.data.request.LoginRequest
import com.francle.hello.feature.auth.login.domain.repository.LoginRepository
import com.francle.hello.feature.auth.splash.data.response.AuthResult
import com.onesignal.OneSignal
import retrofit2.HttpException

class LoginRepositoryImpl(
    private val api: LoginApi,
    private val pref: SharedPreferences
) : LoginRepository {
    override suspend fun login(
        email: String,
        password: String
    ): AuthResult<String> {
        return try {
            val response = api.login(
                LoginRequest(
                    email = email,
                    password = password
                )
            )
            pref.edit()
                .putString(Constants.KEY_JWT_TOKEN, response.token)
                .putString(Constants.KEY_USER_ID, response.userId)
                .putString(Constants.KEY_STREAM_TOKEN, response.streamToken)
                .putString(Constants.KEY_USER_NAME, response.username)
                .apply()
            OneSignal.setExternalUserId(response.userId)
            AuthResult.Authorized()
        } catch (e: HttpException) {
            if (e.code() == 401) {
                AuthResult.Unauthorized("Password does not match.")
            } else if (e.code() == 409) {
                AuthResult.UnknownError("Incorrect email or password.")
            } else {
                AuthResult.UnknownError()
            }
        } catch (e: Exception) {
            AuthResult.UnknownError()
        }
    }
}
