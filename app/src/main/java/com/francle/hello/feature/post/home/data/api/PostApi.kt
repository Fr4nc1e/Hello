package com.francle.hello.feature.post.home.data.api

import com.francle.hello.feature.home.data.response.GetPostByPostIdResponse
import com.francle.hello.feature.home.data.response.GetPostByUserIdResponse
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Query

interface PostApi {
    @GET("/api/post/get")
    suspend fun getPost(
        @Query("postId") id: String
    ): GetPostByPostIdResponse?

    @GET("/api/posts/get")
    suspend fun getPosts(
        @Query("userId") userId: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): GetPostByUserIdResponse?

    @DELETE("/api/post/delete")
    suspend fun deletePostByPostId(
        @Query("postId") postId: String
    )

    @DELETE("/api/posts/delete")
    suspend fun deletePostsByUserId(
        @Query("userId") userId: String
    )
}
