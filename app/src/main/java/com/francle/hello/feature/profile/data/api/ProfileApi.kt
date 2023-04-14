package com.francle.hello.feature.profile.data.api

import com.francle.hello.core.util.Constants
import com.francle.hello.feature.profile.data.response.EditProfileResponse
import com.francle.hello.feature.profile.data.response.UserProfileResponse
import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query

interface ProfileApi {
    @GET("/api/user/profile")
    suspend fun getUserProfile(
        @Query(Constants.PARAM_USER_ID) userId: String
    ): UserProfileResponse?

    @Multipart
    @PUT("/api/user/profile/edit")
    suspend fun editProfile(
        @Part editProfileRequest: MultipartBody.Part?,
        @Part profileImage: MultipartBody.Part?,
        @Part bannerImage: MultipartBody.Part?
    ): EditProfileResponse?
}
