package com.francle.hello.feature.communication.data.repository

import com.francle.hello.R
import com.francle.hello.core.data.call.Resource
import com.francle.hello.core.ui.util.UiText
import com.francle.hello.feature.communication.data.api.ChatApi
import com.francle.hello.feature.communication.data.request.CreateChannelRequest
import com.francle.hello.feature.communication.data.request.GetChannelIdRequest
import com.francle.hello.feature.communication.domain.repository.ChatRepository
import java.io.IOException
import retrofit2.HttpException

class ChatRepositoryImpl(
    private val api: ChatApi
) : ChatRepository {
    override suspend fun createChatChannle(
        memberIds: Set<String>,
        streamChannelId: String
    ): Resource<Unit> {
        return try {
            api.createChatChannel(
                CreateChannelRequest(
                    memberIds = memberIds,
                    streamChannelId = streamChannelId
                )
            )
            Resource.Success()
        } catch (e: HttpException) {
            Resource.Error(message = UiText.StringResource(R.string.network_error_happens))
        } catch (e: IOException) {
            Resource.Error(message = UiText.StringResource(R.string.local_error_happens))
        }
    }

    override suspend fun getChannelId(memberIds: Set<String>): Resource<String?> {
        return try {
            val response = api.getChannelId(GetChannelIdRequest(memberIds))
            Resource.Success(response.streamChannelId)
        } catch (e: HttpException) {
            Resource.Error(message = UiText.StringResource(R.string.network_error_happens))
        } catch (e: IOException) {
            Resource.Error(message = UiText.StringResource(R.string.local_error_happens))
        }
    }
}
