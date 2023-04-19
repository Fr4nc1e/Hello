package com.francle.hello.feature.communication.ui.viewmodel

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.francle.hello.R
import com.francle.hello.core.ui.event.UiEvent
import com.francle.hello.core.ui.util.UiText
import com.francle.hello.core.util.Constants.KEY_PROFILE_IMAGE_URL
import com.francle.hello.core.util.Constants.KEY_STREAM_TOKEN
import com.francle.hello.core.util.Constants.KEY_USER_ID
import com.francle.hello.core.util.Constants.KEY_USER_NAME
import com.francle.hello.feature.communication.ui.presentation.event.ChatEvent
import com.francle.hello.feature.communication.ui.presentation.message.MessageActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.api.models.querysort.QuerySortByField
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.compose.viewmodel.channels.ChannelViewModelFactory
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ChatViewModel @Inject constructor(
    sharedPreferences: SharedPreferences,
    client: ChatClient,
    private val application: Application
) : ViewModel() {
    private val _clickHeaderMenu = MutableStateFlow(false)
    val clickHeaderMenu = _clickHeaderMenu.asStateFlow()

    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    private val _resultChannel = Channel<UiEvent>()
    val resultChannel = _resultChannel.receiveAsFlow()

    val factory by lazy {
        ChannelViewModelFactory(
            ChatClient.instance(),
            QuerySortByField.descByName("last_updated"),
            null
        )
    }

    init {
        if (ChatClient.instance().getCurrentUser() == null) {
            client.connectUser(
                user = User(
                    id = sharedPreferences.getString(KEY_USER_ID, "") ?: "",
                    name = sharedPreferences.getString(KEY_USER_NAME, "") ?: "",
                    image = sharedPreferences.getString(KEY_PROFILE_IMAGE_URL, "") ?: ""
                ),
                token = sharedPreferences.getString(KEY_STREAM_TOKEN, "") ?: ""
            ).enqueue { result ->
                if (result.isSuccess) {
                    viewModelScope.launch { 
                        _resultChannel.send(
                            UiEvent.Message(
                                UiText.StringResource(R.string.connected)
                            )
                        )
                    }
                }
            }
        }
    }

    fun onEvent(event: ChatEvent) {
        when (event) {
            is ChatEvent.ClickMessageItem -> {
                application.startActivity(
                    MessageActivity.createIntent(
                        application.applicationContext,
                        channelId = event.channelId,
                        messageId = null
                    )
                )
            }

            ChatEvent.ClickHeaderMenu -> {
                _clickHeaderMenu.update { !it }
            }

            is ChatEvent.InputQuery -> {
                _query.update { event.query }
            }
        }
    }
}
