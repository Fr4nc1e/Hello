package com.francle.hello.feature.post.like.data.response

import com.francle.hello.feature.post.like.domain.models.SingleUserInfo

data class LikeUserInfoResponse(
    val userId: String,
    val username: String,
    val profileImageUrl: String?
) {
    fun toSingleUserInfo() = SingleUserInfo(
        userId = userId,
        username = username,
        profileImageUrl = profileImageUrl
    )
}
