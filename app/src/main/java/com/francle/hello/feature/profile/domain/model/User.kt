package com.francle.hello.feature.profile.domain.model

data class User(
    val userId: String,
    val email: String,
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
)
