package com.francle.hello.feature.createpost.data.api

import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface CreatePostApi {
    @Multipart
    @POST("/api/post/create")
    suspend fun createPost(
        @Part postData: MultipartBody.Part?,
        @Part postContent: List<MultipartBody.Part>?
    )
}
