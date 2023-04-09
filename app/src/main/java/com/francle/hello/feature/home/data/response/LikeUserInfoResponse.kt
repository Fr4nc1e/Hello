package com.francle.hello.feature.home.data.response

import com.francle.hello.feature.home.domain.model.SingleUserInfo

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
