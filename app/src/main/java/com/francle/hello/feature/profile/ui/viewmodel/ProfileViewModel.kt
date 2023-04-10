package com.francle.hello.feature.profile.ui.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.francle.hello.R
import com.francle.hello.core.data.call.Resource
import com.francle.hello.core.ui.event.UiEvent
import com.francle.hello.core.ui.util.UiText
import com.francle.hello.core.util.Constants
import com.francle.hello.feature.profile.domain.model.User
import com.francle.hello.feature.profile.domain.repository.ProfileRepository
import com.francle.hello.feature.profile.ui.event.ProfileEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val sharedPreferences: SharedPreferences,
    private val profileRepository: ProfileRepository
) : ViewModel() {
    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()

    private val _showDropMenu = MutableStateFlow(false)
    val showDropMenu = _showDropMenu.asStateFlow()

    private val _showAlertDialog = MutableStateFlow(false)
    val showAlertDialog = _showAlertDialog.asStateFlow()

    private val _isOwnProfile = MutableStateFlow(false)
    val isOwnProfile = _isOwnProfile.asStateFlow()

    private val _resultChannel = Channel<UiEvent>()
    val resultChannel = _resultChannel.receiveAsFlow()

    init {
        savedStateHandle.get<String>("userId")?.also { userId ->
            viewModelScope.launch {
                profileRepository.getUserProfile(userId).collect { result ->
                    when (result) {
                        is Resource.Error -> {
                            _resultChannel.send(UiEvent.Message(
                                result.message ?: UiText.StringResource(
                                R.string.an_unknown_error_occurred)))
                        }
                        is Resource.Success -> {
                            _user.update { result.data }
                            _isOwnProfile.update {
                                userId == (sharedPreferences.getString(Constants.KEY_USER_ID, "")
                                    ?: "")
                            }
                        }
                    }
                }
            }
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
                _showAlertDialog.update { !it }
            }
        }
    }
}
