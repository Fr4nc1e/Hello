package com.francle.hello.feature.home.domain.models

data class Post(
    val userId: String,
    val username: String?,
    val hashTag: String?,
    val profileImageUrl: String?,
    val postText: String?,
    val postContentPairs: List<PostContentPair>?,
    val timestamp: Long,
    val likeCount: Int = 0,
    val commentCount: Int = 0,
    val id: String = ""
)
