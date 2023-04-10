package com.francle.hello.feature.post.like.domain.repository

import com.francle.hello.core.data.util.call.Resource
import com.francle.hello.feature.post.like.data.request.LikeRequest
import com.francle.hello.feature.post.like.domain.models.SingleUserInfo

interface LikeRepository {
    suspend fun like(likeRequest: LikeRequest): Resource<Unit>

    suspend fun dislike(
        arrowBackUserId: String,
        arrowForwardUserId: String,
        arrowForwardEntityId: String,
        arrowForwardEntityType: Int
    ): Resource<Unit>

    suspend fun getLikeUserList(arrowForwardEntityId: String): Resource<List<SingleUserInfo>>

    suspend fun checkLikeState(
        arrowBackUserId: String,
        arrowForwardEntityId: String
    ): Resource<Boolean>
}
