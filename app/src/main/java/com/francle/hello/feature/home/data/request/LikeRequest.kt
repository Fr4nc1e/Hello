package com.francle.hello.feature.home.data.request

data class LikeRequest(
    val arrowBackUserId: String,
    val arrowForwardUserId: String,
    val arrowForwardEntityId: String,
    val arrowForwardEntityType: Int
)
