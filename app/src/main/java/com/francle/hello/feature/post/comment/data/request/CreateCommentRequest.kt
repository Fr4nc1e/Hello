package com.francle.hello.feature.post.comment.data.request

data class CreateCommentRequest(
    val arrowForwardUserId: String,
    val arrowForwardEntityId: String,
    val arrowForwardEntityType: Int,
    val commentText: String?
)
