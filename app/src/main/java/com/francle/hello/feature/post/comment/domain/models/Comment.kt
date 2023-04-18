package com.francle.hello.feature.post.comment.domain.models

data class Comment(
    val commentId: String,
    val arrowBackUserId: String,
    val arrowBackUsername: String,
    val hashTag: String,
    val profileImageUrl: String,
    val arrowForwardUserId: String,
    val arrowForwardEntityId: String,
    val arrowForwardEntityType: Int,
    val commentText: String?,
    val commentMediaUrls: List<String>?,
    val likeCount: Int,
    val commentCount: Int,
    val timestamp: Long
)
