package com.francle.hello.feature.communication.ui.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.francle.hello.R
import com.francle.hello.core.ui.event.UiEvent
import com.francle.hello.core.ui.hub.presentation.navigation.destination.Destination
import com.francle.hello.core.ui.util.UiText
import com.francle.hello.core.util.Constants.KEY_PROFILE_IMAGE_URL
import com.francle.hello.core.util.Constants.KEY_STREAM_TOKEN
import com.francle.hello.core.util.Constants.KEY_USER_ID
import com.francle.hello.core.util.Constants.KEY_USER_NAME
import com.francle.hello.feature.communication.ui.presentation.event.ChatEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.models.User
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ChatViewModel @Inject constructor(
    sharedPreferences: SharedPreferences,
    client: ChatClient
) : ViewModel() {
    private val _resultChannel = Channel<UiEvent>()
    val resultChannel = _resultChannel.receiveAsFlow()

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
                viewModelScope.launch {
                    _resultChannel.send(
                        UiEvent.Navigate(
                            Destination.Message.route + "/${event.channelId}"
                        )
                    )
                }
            }
        }
    }
}
