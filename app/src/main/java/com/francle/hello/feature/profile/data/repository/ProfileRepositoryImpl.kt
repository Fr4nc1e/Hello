package com.francle.hello.feature.profile.data.repository

import com.francle.hello.R
import com.francle.hello.core.data.call.Resource
import com.francle.hello.core.ui.util.UiText
import com.francle.hello.feature.profile.data.api.ProfileApi
import com.francle.hello.feature.profile.domain.model.User
import com.francle.hello.feature.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class ProfileRepositoryImpl(
    private val profileApi: ProfileApi
) : ProfileRepository {
    override fun getUserProfile(userId: String): Flow<Resource<User?>> {
        return flow {
            try {
                profileApi.getUserProfile(userId)?.let { userProfileResponse ->
                    emit(Resource.Success(userProfileResponse.toUser()))
                } ?: emit(Resource.Success(null))
            } catch (e: HttpException) {
                emit(Resource.Error(message = UiText.StringResource(R.string.network_error_happens)))
            } catch (e: IOException) {
                emit(Resource.Error(message = UiText.StringResource(R.string.local_error_happens)))
            }
        }
    }
}