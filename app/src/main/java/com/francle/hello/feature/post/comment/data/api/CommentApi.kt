package com.francle.hello.feature.post.comment.data.api

import com.francle.hello.feature.post.comment.data.response.GetEntityCommentsResponse
import com.francle.hello.feature.post.comment.data.response.GetUserCommentsResponse
import okhttp3.MultipartBody
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface CommentApi {
    @Multipart
    @POST("/api/comment/create")
    suspend fun createComment(
        @Part commentData: MultipartBody.Part?,
        @Part commentMediaContent: List<MultipartBody.Part>?
    )

    @GET("/api/comment/entity")
    suspend fun getCommentsOfEntity(
        @Query("arrow_forward_entity_id") entityId: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): GetEntityCommentsResponse

    @GET("/api/comment/user")
    suspend fun getCommentsOfUser(
        @Query("userId") userId: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): GetUserCommentsResponse

    @DELETE("/api/comment/delete")
    suspend fun deleteComment(
        @Query("commentId") commentId: String
    )
}
