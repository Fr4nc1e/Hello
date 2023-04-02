package com.francle.hello.feature.home.domain.repository

import com.francle.hello.core.data.util.call.Resource
import com.francle.hello.feature.home.domain.model.Post
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    fun getPost(postId: String): Flow<Resource<Post?>>

    fun getPosts(
        userId: String,
        page: Int,
        pageSize: Int
    ): Flow<Resource<List<Post?>?>>

    suspend fun deletePostByPostId(postId: String): Resource<Unit>

    suspend fun deletePostsByUserId(userId: String): Resource<Unit>
}
