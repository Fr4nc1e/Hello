package com.francle.hello.feature.communication.di

import android.app.Application
import com.francle.hello.core.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.offline.model.message.attachments.UploadAttachmentsNetworkType
import io.getstream.chat.android.offline.plugin.configuration.Config
import io.getstream.chat.android.offline.plugin.factory.StreamOfflinePluginFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ChatModule {
    @Provides
    @Singleton
    fun provideChatClient(app: Application): ChatClient {
        return ChatClient.Builder(
            apiKey = Constants.STREAM_SDK_API_KEY,
            appContext = app
        )
            .withPlugin(
                StreamOfflinePluginFactory(
                    config = Config(
                        backgroundSyncEnabled = true,
                        userPresence = true,
                        persistenceEnabled = true,
                        uploadAttachmentsNetworkType = UploadAttachmentsNetworkType.NOT_ROAMING
                    ),
                    appContext = app
                )
            )
            .logLevel(ChatLogLevel.ALL)
            .build()
    }
}
