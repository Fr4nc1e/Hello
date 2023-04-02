package com.francle.hello.feature.home.data.response

import com.francle.hello.feature.home.domain.model.Post
import com.francle.hello.feature.home.domain.model.PostContentPair

data class PostResponse(
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
) {
    fun toPost(): Post {
        return Post(
            userId = userId,
            username = username,
            hashTag = hashTag,
            profileImageUrl = profileImageUrl,
            postText = postText,
            postContentPairs = postContentPairs,
            timestamp = timestamp,
            likeCount = likeCount,
            commentCount = commentCount,
            id = id
        )
    }
}
