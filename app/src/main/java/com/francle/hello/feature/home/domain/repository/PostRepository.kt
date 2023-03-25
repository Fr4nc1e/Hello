package com.francle.hello.feature.home.domain.repository

import android.net.Uri
import com.francle.hello.core.data.util.Resource
import com.francle.hello.feature.home.data.response.CreatePostResponse
import com.francle.hello.feature.home.domain.model.Post
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    fun getPost(postId: String): Flow<Resource<Post?>>

    fun getPosts(
        userId: String,
        page: Int,
        pageSize: Int
    ): Flow<Resource<List<Post?>?>>

    fun createPost(
        postData: String?,
        postContent: List<Uri>?
    ): Flow<Resource<CreatePostResponse?>>

    suspend fun deletePostByPostId(postId: String): Resource<Unit>

    suspend fun deletePostsByUserId(userId: String): Resource<Unit>
}
