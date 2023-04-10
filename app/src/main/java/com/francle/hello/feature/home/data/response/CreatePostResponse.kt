package com.francle.hello.feature.home.data.response

import com.francle.hello.feature.home.domain.models.PostContentPair

data class CreatePostResponse(
    val postContentPair: List<PostContentPair?>?
)
