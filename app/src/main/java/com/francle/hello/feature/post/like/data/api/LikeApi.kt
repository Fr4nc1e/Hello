package com.francle.hello.feature.post.like.data.api

import com.francle.hello.core.util.Constants
import com.francle.hello.feature.post.like.data.request.LikeRequest
import com.francle.hello.feature.post.like.data.response.LikeUserInfoResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface LikeApi {
    @POST("/api/like")
    suspend fun like(
        @Body likeRequest: LikeRequest
    )

    @DELETE("/api/dislike")
    suspend fun dislike(
        @Query(Constants.ARROW_BACK_USER_ID) arrowBackUserId: String,
        @Query(Constants.ARROW_FORWARD_USER_ID) arrowForwardUserId: String,
        @Query(Constants.ARROW_FORWARD_ENTITY_ID) arrowForwardEntityId: String,
        @Query(Constants.ARROW_FORWARD_ENTITY_TYPE) arrowForwardEntityType: Int
    )

    @GET("/api/like/users")
    suspend fun getLikeUserList(
        @Query(Constants.ARROW_FORWARD_ENTITY_ID) arrowForwardEntityId: String
    ): List<LikeUserInfoResponse>

    @GET("/api/like/state")
    suspend fun checkLikeState(
        @Query(Constants.ARROW_BACK_USER_ID) arrowBackUserId: String,
        @Query(Constants.ARROW_FORWARD_ENTITY_ID) arrowForwardEntityId: String
    ): Boolean
}
