package com.francle.hello.feature.post.comment.domain.repository

import android.net.Uri
import com.francle.hello.core.data.call.Resource
import com.francle.hello.feature.post.comment.domain.models.Comment
import kotlinx.coroutines.flow.Flow

interface CommentRepository {
    suspend fun createComment(
        arrowForwardUserId: String,
        arrowForwardEntityId: String,
        arrowForwardEntityType: Int,
        commentText: String?,
        commentMediaUris: List<Uri>?
    ): Resource<Unit>

    fun getCommentsOfEntity(
        entityId: String,
        page: Int,
        pageSize: Int
    ): Flow<Resource<List<Comment?>?>>

    fun getCommentsOfUser(
        userId: String,
        page: Int,
        pageSize: Int
    ): Flow<Resource<List<Comment?>?>>

    suspend fun deleteComment(
        commentId: String
    ): Resource<Unit>
}
