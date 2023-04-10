package com.francle.hello.feature.profile.data.api

import com.francle.hello.core.util.Constants
import com.francle.hello.feature.profile.data.response.UserProfileResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ProfileApi {
    @GET("/api/user/profile")
    suspend fun getUserProfile(
        @Query(Constants.PARAM_USER_ID) userId: String
    ): UserProfileResponse?
}