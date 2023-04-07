package com.francle.hello.feature.pair.data.response

import com.francle.hello.feature.pair.domain.models.PairUser

data class PairResponse(
    val userId: String,
    val username: String,
    val hashTag: String,
    val age: Int? = null,
    val profileImageUrl: String? = null,
    val bannerImageUrl: String? = null,
    val hobbies: List<String>? = null,
    val following: Int = 0,
    val followedBy: Int = 0,
    val likedCount: Int = 0,
    val postCount: Int = 0,
    val bio: String? = null
) {
    fun toPairUser() = PairUser(
        userId = userId,
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
