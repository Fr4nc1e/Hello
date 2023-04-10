package com.francle.hello.feature.profile.data.response

import com.francle.hello.feature.profile.domain.model.User

data class UserProfileResponse(
    val userId: String,
    val email: String,
    val username: String,
    val hashTag: String,
    val age: Int?,
    val profileImageUrl: String?,
    val bannerImageUrl: String?,
    val hobbies: List<String>?,
    val following: Int,
    val followedBy: Int,
    val likedCount: Int,
    val postCount: Int,
    val bio: String?
) {
    fun toUser() = User(
        userId = userId,
        email = email,
        username = username,
        hashTag = hashTag,
        age = age,
        profileImageUrl = profileImageUrl,
        bannerImageUrl = bannerImageUrl,
        hobbies = hobbies,
        following = following,
        followedBy = followedBy,
        likedCount = likedCount,
        postCount = postCount,
        bio = bio
    )
}
