package com.francle.hello.feature.communication.domain.repository

import com.francle.hello.core.data.call.Resource

interface ChatRepository {
    suspend fun createChatChannle(
        memberIds: Set<String>,
        streamChannelId: String
    ): Resource<Unit>

    suspend fun getChannelId(memberIds: Set<String>): Resource<String?>
}
