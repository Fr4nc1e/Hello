package com.francle.hello.feature.profile.ui.viewmodel

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.francle.hello.R
import com.francle.hello.core.data.call.Resource
import com.francle.hello.core.ui.event.UiEvent
import com.francle.hello.core.ui.hub.presentation.navigation.destination.Destination
import com.francle.hello.core.ui.util.UiText
import com.francle.hello.core.util.Constants
import com.francle.hello.feature.communication.domain.repository.ChatRepository
import com.francle.hello.feature.communication.ui.presentation.message.MessageActivity
import com.francle.hello.feature.profile.domain.model.User
import com.francle.hello.feature.profile.domain.repository.ProfileRepository
import com.francle.hello.feature.profile.ui.event.ProfileEvent
import com.francle.hello.feature.profile.ui.presentation.ProfileTabContent
import dagger.hilt.android.lifecycle.HiltViewModel
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.api.models.QueryChannelRequest
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val sharedPreferences: SharedPreferences,
    private val profileRepository: ProfileRepository,
    private val chatRepository: ChatRepository,
    private val client: ChatClient,
    private val application: Application
) : ViewModel() {
    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()

    private val _ownUserId = MutableStateFlow("")
    val ownUserId = _ownUserId.asStateFlow()

    private val _showDropMenu = MutableStateFlow(false)
    val showDropMenu = _showDropMenu.asStateFlow()

    private val _showLogOutDialog = MutableStateFlow(false)
    val showLogOutDialog = _showLogOutDialog.asStateFlow()

    private val _isOwnProfile = MutableStateFlow(false)
    val isOwnProfile = _isOwnProfile.asStateFlow()

    private val _tabIndex = MutableStateFlow(ProfileTabContent.Posts.ordinal)
    val tabIndex = _tabIndex.asStateFlow()

    private val _resultChannel = Channel<UiEvent>()
    val resultChannel = _resultChannel.receiveAsFlow()

    init {
        _ownUserId.update {
            sharedPreferences.getString(Constants.KEY_USER_ID, "") ?: ""
        }
        savedStateHandle.get<String>("userId")?.also { userId ->
            getProfile(userId)
        }
    }

    fun onEvent(event: ProfileEvent) {
        when (event) {
            ProfileEvent.ClickMoreVert -> {
                _showDropMenu.update { !it }
            }

            ProfileEvent.LogOut -> {
                viewModelScope.launch {
                    sharedPreferences.edit()
                        .remove(Constants.KEY_USER_ID)
                        .remove(Constants.KEY_JWT_TOKEN)
                        .apply()
                    _resultChannel.send(UiEvent.LogOut)
                }
            }

            ProfileEvent.ClickLogOut -> {
                _showLogOutDialog.update { !it }
            }

            ProfileEvent.ClickMessage -> {
                _user.value?.userId?.also { remoteUserId ->
                    viewModelScope.launch {
                        chatRepository.getChannelId(
                            setOf(_ownUserId.value, remoteUserId)
                        ).also { result ->
                            when (result) {
                                is Resource.Error -> {
                                    result.message?.let {
                                        _resultChannel.send(UiEvent.Message(it))
                                    }
                                }
                                is Resource.Success -> {
                                    val channelId = result.data
                                    when (channelId == null) {
                                        true -> {
                                            val createdChannelId = UUID.randomUUID().toString()
                                            chatRepository.createChatChannle(
                                                memberIds = setOf(_ownUserId.value, remoteUserId),
                                                streamChannelId = createdChannelId
                                            ).also {
                                                when (it) {
                                                    is Resource.Error -> {
                                                        result.message?.let { uiText ->
                                                            _resultChannel.send(
                                                                UiEvent.Message(uiText)
                                                            )
                                                        }
                                                    }
                                                    is Resource.Success -> {
                                                        client.createChannel(
                                                            channelType = "messaging",
                                                            channelId = createdChannelId,
                                                            memberIds = listOf(
                                                                _ownUserId.value,
                                                                (_user.value?.userId ?: "")
                                                            ),
                                                            extraData = mapOf(
                                                                "name" to (_user.value?.username ?: "User")
                                                            )
                                                        ).enqueue { channelResult ->
                                                            if (channelResult.isSuccess) {
                                                                application.startActivity(
                                                                    MessageActivity.createIntent(
                                                                        application.applicationContext,
                                                                        channelId = channelResult.data().cid,
                                                                        messageId = null
                                                                    )
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        false -> {
                                            client.queryChannel(
                                                channelType = "messaging",
                                                channelId = channelId,
                                                request = QueryChannelRequest()
                                            ).enqueue { channelResult ->
                                                application.startActivity(
                                                    MessageActivity.createIntent(
                                                        application.applicationContext,
                                                        channelId = channelResult.data().cid,
                                                        messageId = null
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            ProfileEvent.ClickEdit -> {
                viewModelScope.launch {
                    _resultChannel.send(UiEvent.Navigate(Destination.EditProfile.route))
                }
            }

            is ProfileEvent.SwitchTab -> {
                _tabIndex.update { event.index }
            }
        }
    }

    fun getProfile(userId: String) {
        viewModelScope.launch {
            profileRepository.getUserProfile(userId).collect { result ->
                when (result) {
                    is Resource.Error -> {
                        _resultChannel.send(
                            UiEvent.Message(
                                result.message ?: UiText.StringResource(
                                    R.string.an_unknown_error_occurred
                                )
                            )
                        )
                    }
                    is Resource.Success -> {
                        _user.update { result.data }
                        _isOwnProfile.update {
                            userId == _ownUserId.value
                        }
                    }
                }
            }
        }
    }
}
