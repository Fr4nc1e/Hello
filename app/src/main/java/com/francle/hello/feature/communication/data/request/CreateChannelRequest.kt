package com.francle.hello.feature.communication.data.request

data class CreateChannelRequest(
    val memberIds: Set<String>,
    val streamChannelId: String
)
