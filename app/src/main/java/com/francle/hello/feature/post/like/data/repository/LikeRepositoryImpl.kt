package com.francle.hello.feature.post.like.data.repository

import com.francle.hello.R
import com.francle.hello.core.data.util.call.Resource
import com.francle.hello.core.ui.util.UiText
import com.francle.hello.feature.post.like.data.api.LikeApi
import com.francle.hello.feature.post.like.data.request.LikeRequest
import com.francle.hello.feature.post.like.domain.models.SingleUserInfo
import com.francle.hello.feature.post.like.domain.repository.LikeRepository
import java.io.IOException
import retrofit2.HttpException

class LikeRepositoryImpl(
    private val likeApi: LikeApi
) : LikeRepository {
    override suspend fun like(likeRequest: LikeRequest): Resource<Unit> {
        return try {
            likeApi.like(likeRequest)
            Resource.Success()
        } catch (e: HttpException) {
            Resource.Error(message = UiText.StringResource(R.string.network_error_happens))
        } catch (e: IOException) {
            Resource.Error(message = UiText.StringResource(R.string.local_error_happens))
        }
    }

    override suspend fun dislike(
        arrowBackUserId: String,
        arrowForwardUserId: String,
        arrowForwardEntityId: String,
        arrowForwardEntityType: Int
    ): Resource<Unit> {
        return try {
            likeApi.dislike(
                arrowBackUserId = arrowBackUserId,
                arrowForwardUserId = arrowForwardUserId,
                arrowForwardEntityId = arrowForwardEntityId,
                arrowForwardEntityType = arrowForwardEntityType
            )
            Resource.Success()
        } catch (e: HttpException) {
            Resource.Error(message = UiText.StringResource(R.string.network_error_happens))
        } catch (e: IOException) {
            Resource.Error(message = UiText.StringResource(R.string.local_error_happens))
        }
    }

    override suspend fun getLikeUserList(
        arrowForwardEntityId: String
    ): Resource<List<SingleUserInfo>> {
        return try {
            val response = likeApi.getLikeUserList(arrowForwardEntityId).map {
                it.toSingleUserInfo()
            }
            Resource.Success(data = response)
        } catch (e: HttpException) {
            Resource.Error(
                data = emptyList(),
                message = UiText.StringResource(R.string.network_error_happens)
            )
        } catch (e: IOException) {
            Resource.Error(
                data = emptyList(),
                message = UiText.StringResource(R.string.local_error_happens)
            )
        }
    }

    override suspend fun checkLikeState(
        arrowBackUserId: String,
        arrowForwardEntityId: String
    ): Resource<Boolean> {
        return try {
            likeApi.checkLikeState(
                arrowBackUserId = arrowBackUserId,
                arrowForwardEntityId = arrowForwardEntityId
            ).let {
                Resource.Success(it)
            }
        } catch (e: HttpException) {
            Resource.Error(
                data = false,
                message = UiText.StringResource(R.string.network_error_happens)
            )
        } catch (e: IOException) {
            Resource.Error(
                data = false,
                message = UiText.StringResource(R.string.local_error_happens)
            )
        }
    }
}
