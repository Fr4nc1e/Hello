package com.francle.hello.feature.profile.domain.repository

import android.net.Uri
import com.francle.hello.core.data.call.Resource
import com.francle.hello.feature.profile.data.response.EditProfileResponse
import com.francle.hello.feature.profile.domain.model.User
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getUserProfile(userId: String): Flow<Resource<User?>>
    suspend fun editProfile(
        username: String?,
        age: Int?,
        bio: String?,
        profileImageUri: Uri?,
        bannerImageUri: Uri?
    ): Resource<EditProfileResponse?>
}
