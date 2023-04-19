package com.francle.hello.feature.communication.ui.presentation.message

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import com.getstream.sdk.chat.utils.DateFormatter
import io.getstream.chat.android.compose.ui.messages.MessagesScreen
import io.getstream.chat.android.compose.ui.theme.ChatTheme

class MessageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val channelId = intent.getStringExtra(KEY_CHANNEL_ID) ?: return
        val messageId = intent.getStringExtra(KEY_MESSAGE_ID)

        setContent {
            ChatTheme(
                dateFormatter = DateFormatter.from(this),
                isInDarkMode = isSystemInDarkTheme()
            ) {
                MessagesScreen(
                    channelId = channelId,
                    onBackPressed = { finish() },
                    onHeaderActionClick = {},
                    messageId = messageId,
                    navigateToThreadViaNotification = true
                )
            }
        }
    }

    companion object {
        private const val KEY_CHANNEL_ID = "channelId"
        private const val KEY_MESSAGE_ID = "messageId"

        fun createIntent(
            context: Context,
            channelId: String,
            messageId: String?
        ): Intent {
            return Intent(context, MessageActivity::class.java).apply {
                addFlags(FLAG_ACTIVITY_NEW_TASK)
                putExtra(KEY_CHANNEL_ID, channelId)
                if (messageId != null) {
                    putExtra(KEY_MESSAGE_ID, messageId)
                }
            }
        }
    }
}
