package com.francle.hello.feature.post.comment.data.response

import com.francle.hello.feature.post.comment.domain.models.Comment

data class CommentResponse(
    val commentId: String,
    val arrowBackUserId: String,
    val arrowBackUsername: String,
    val arrowBackHashTag: String,
    val profileImageUrl: String,
    val arrowForwardUserId: String,
    val arrowForwardEntityId: String,
    val arrowForwardEntityType: Int,
    val commentText: String?,
    val commentMediaUrls: List<String>?,
    val likeCount: Int,
    val commentCount: Int,
    val timestamp: Long
) {
    fun toComment() = Comment(
        commentId = commentId,
        arrowBackUserId = arrowBackUserId,
        arrowBackUsername = arrowBackUsername,
        hashTag = arrowBackHashTag,
        profileImageUrl = profileImageUrl,
        arrowForwardUserId = arrowForwardUserId,
        arrowForwardEntityId = arrowForwardEntityId,
        arrowForwardEntityType = arrowForwardEntityType,
        commentText = commentText,
        commentMediaUrls = commentMediaUrls,
        likeCount = likeCount,
        commentCount = commentCount,
        timestamp = timestamp
    )
}
