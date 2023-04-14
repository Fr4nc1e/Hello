package com.francle.hello.feature.profile.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import com.francle.hello.R
import com.francle.hello.core.data.call.Resource
import com.francle.hello.core.data.file.getFileName
import com.francle.hello.core.ui.util.UiText
import com.francle.hello.feature.profile.data.api.ProfileApi
import com.francle.hello.feature.profile.data.request.EditProfileRequest
import com.francle.hello.feature.profile.data.response.EditProfileResponse
import com.francle.hello.feature.profile.domain.model.User
import com.francle.hello.feature.profile.domain.repository.ProfileRepository
import com.google.gson.Gson
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException

class ProfileRepositoryImpl(
    private val profileApi: ProfileApi,
    private val context: Context,
    private val gson: Gson
) : ProfileRepository {
    override fun getUserProfile(userId: String): Flow<Resource<User?>> {
        return flow {
            try {
                profileApi.getUserProfile(userId)?.let { userProfileResponse ->
                    emit(Resource.Success(userProfileResponse.toUser()))
                } ?: emit(Resource.Success(null))
            } catch (e: HttpException) {
                emit(
                    Resource.Error(message = UiText.StringResource(R.string.network_error_happens))
                )
            } catch (e: IOException) {
                emit(Resource.Error(message = UiText.StringResource(R.string.local_error_happens)))
            }
        }
    }

    @SuppressLint("Recycle")
    override suspend fun editProfile(
        username: String?,
        age: Int?,
        bio: String?,
        profileImageUri: Uri?,
        bannerImageUri: Uri?
    ): Resource<EditProfileResponse?> {
        return try {
            val editProfileRequest = EditProfileRequest(
                username = username,
                age = age,
                bio = bio
            )
            val editRequest = editProfileRequest.let {
                MultipartBody.Part.createFormData(
                    name = "editRequest",
                    value = gson.toJson(it)
                )
            }
            val profileImageFile = profileImageUri?.let { uri ->
                withContext(Dispatchers.IO) {
                    context.contentResolver.openFileDescriptor(uri, "r")?.let {
                        val inputStream = FileInputStream(it.fileDescriptor)
                        val file = File(
                            context.cacheDir,
                            context.contentResolver.getFileName(uri)
                        )
                        val outputStream = FileOutputStream(file)
                        inputStream.copyTo(outputStream)
                        file
                    }
                }
            }
            val profileImage = profileImageFile?.let {
                MultipartBody.Part.createFormData(
                    name = "profileImage",
                    filename = it.name,
                    body = it.asRequestBody()
                )
            }
            val bannerImageFile = bannerImageUri?.let { uri ->
                withContext(Dispatchers.IO) {
                    context.contentResolver.openFileDescriptor(uri, "r")?.let {
                        val inputStream = FileInputStream(it.fileDescriptor)
                        val file = File(
                            context.cacheDir,
                            context.contentResolver.getFileName(uri)
                        )
                        val outputStream = FileOutputStream(file)
                        inputStream.copyTo(outputStream)
                        file
                    }
                }
            }
            val bannerImage = bannerImageFile?.let {
                MultipartBody.Part.createFormData(
                    name = "bannerImage",
                    filename = it.name,
                    body = it.asRequestBody()
                )
            }
            profileApi.editProfile(
                editProfileRequest = editRequest,
                profileImage = profileImage,
                bannerImage = bannerImage
            ).let {
                Resource.Success(
                    data = it,
                    message = UiText.StringResource(R.string.edit_profile_successfully)
                )
            }
        } catch (e: HttpException) {
            Resource.Error(message = UiText.StringResource(R.string.network_error_happens))
        } catch (e: IOException) {
            Resource.Error(message = UiText.StringResource(R.string.local_error_happens))
        }
    }
}
