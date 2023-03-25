package com.francle.hello.feature.home.data.api

import com.francle.hello.feature.home.data.response.CreatePostResponse
import com.francle.hello.feature.home.data.response.GetPostByPostIdResponse
import com.francle.hello.feature.home.data.response.GetPostByUserIdResponse
import okhttp3.MultipartBody
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
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

    @Multipart
    @POST("/api/post/create")
    suspend fun createPost(
        @Part postData: MultipartBody.Part?,
        @Part postContent: List<MultipartBody.Part>?
    ): CreatePostResponse?

    @DELETE("/api/post/delete")
    suspend fun deletePostByPostId(
        @Query("postId") postId: String
    )

    @DELETE("/api/posts/delete")
    suspend fun deletePostsByUserId(
        @Query("userId") userId: String
    )
}
