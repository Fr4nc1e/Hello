package com.francle.hello.feature.profile.domain.repository

import com.francle.hello.core.data.call.Resource
import com.francle.hello.feature.profile.domain.model.User
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getUserProfile(userId: String): Flow<Resource<User?>>
}