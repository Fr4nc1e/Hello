package com.francle.hello.feature.communication.data.api

import com.francle.hello.feature.communication.data.request.CreateChannelRequest
import com.francle.hello.feature.communication.data.request.GetChannelIdRequest
import com.francle.hello.feature.communication.data.response.ChatChannelIdResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ChatApi {
    @POST("/api/chat/create")
    suspend fun createChatChannel(
        @Body request: CreateChannelRequest
    )

    @POST("/api/chat/id")
    suspend fun getChannelId(
        @Body request: GetChannelIdRequest
    ): ChatChannelIdResponse
}
