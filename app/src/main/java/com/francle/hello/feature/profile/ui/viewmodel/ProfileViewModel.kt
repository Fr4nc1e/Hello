package com.francle.hello.feature.profile.ui.viewmodel

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
import com.francle.hello.feature.profile.domain.model.User
import com.francle.hello.feature.profile.domain.repository.ProfileRepository
import com.francle.hello.feature.profile.ui.event.ProfileEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import io.getstream.chat.android.client.ChatClient
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
    private val client: ChatClient
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
                if (ChatClient.instance().getCurrentUser() == null) {
                    client.connectUser(
                        user = io.getstream.chat.android.client.models.User(
                            id = sharedPreferences.getString(Constants.KEY_USER_ID, "") ?: "",
                            name = sharedPreferences.getString(Constants.KEY_USER_NAME, "") ?: "",
                            image = sharedPreferences.getString(Constants.KEY_PROFILE_IMAGE_URL, "")
                                ?: ""
                        ),
                        token = sharedPreferences.getString(Constants.KEY_STREAM_TOKEN, "") ?: ""
                    ).enqueue()
                }

                client.createChannel(
                    channelType = "messaging",
                    channelId = UUID.randomUUID().toString(),
                    memberIds = listOf(
                        _ownUserId.value,
                        (_user.value?.userId ?: "")
                    ),
                    extraData = mapOf("name" to (_user.value?.username ?: "User"))
                ).enqueue { result ->
                    if (result.isSuccess) {
                        viewModelScope.launch {
                            _resultChannel.send(
                                UiEvent.Navigate(
                                    Destination.Message.route + "/${result.data().id}"
                                )
                            )
                        }
                    }
                }
            }

            ProfileEvent.ClickEdit -> {
                viewModelScope.launch {
                    _resultChannel.send(UiEvent.Navigate(Destination.EditProfile.route))
                }
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
