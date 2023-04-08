package com.francle.hello.feature.register.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.francle.hello.R
import com.francle.hello.core.data.util.call.AuthResult
import com.francle.hello.core.ui.util.TextState
import com.francle.hello.core.ui.util.Validator
import com.francle.hello.feature.register.domain.repository.RegisterRepository
import com.francle.hello.feature.register.ui.presentation.event.RegisterEvent
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: RegisterRepository,
    val player: Player,
    app: Application
) : ViewModel() {
    private val _email = MutableStateFlow(TextState())
    val email = _email.asStateFlow()

    private val _username = MutableStateFlow(TextState())
    val username = _username.asStateFlow()

    private val _hashTag = MutableStateFlow(TextState())
    val hashTag = _hashTag.asStateFlow()

    private val _password = MutableStateFlow(TextState())
    val password = _password.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _isPasswordVisible = MutableStateFlow(false)
    val isPasswordVisible = _isPasswordVisible.asStateFlow()

    private val resultChannel = Channel<AuthResult<String>>()
    val authResults = resultChannel.receiveAsFlow()

    init {
        val packageName = app.packageName
        val videoUri = Uri.parse(
            "android.resource://$packageName/${R.raw.splash_screen_video}"
        )
        player.apply {
            setMediaItem(MediaItem.fromUri(videoUri))
            repeatMode = Player.REPEAT_MODE_ALL
            playWhenReady = true
            prepare()
            volume = 0f
        }
    }

    fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.InputEmail -> {
                _email.update {
                    it.copy(text = event.emailText)
                }
            }
            is RegisterEvent.InputPassword -> {
                _password.update {
                    it.copy(text = event.passwordText)
                }
            }
            is RegisterEvent.InputHashTag -> {
                _hashTag.update {
                    it.copy(text = event.hashTagText)
                }
            }
            is RegisterEvent.InputUsername -> {
                _username.update {
                    it.copy(text = event.usernameText)
                }
            }
            RegisterEvent.PasswordVisible -> {
                _isPasswordVisible.update { !it }
            }
            RegisterEvent.Register -> {
                register()
            }
        }
    }

    private fun register() {
        viewModelScope.launch {
            _loading.update { true }
            _email.update { it.copy(error = Validator.validateEmail(_email.value.text)) }
            _username.update { it.copy(error = Validator.validateUsername(_username.value.text)) }
            _hashTag.update { it.copy(error = Validator.validateHashTag(_hashTag.value.text)) }
            _password.update { it.copy(error = Validator.validatePassword(_password.value.text)) }
            if (_email.value.error != null ||
                _username.value.error != null ||
                _hashTag.value.error != null ||
                _password.value.error != null
            ) {
                _loading.update { false }
                return@launch
            }
            resultChannel.send(
                repository.register(
                    email = _email.value.text,
                    username = _username.value.text,
                    hashTag = _hashTag.value.text,
                    password = _password.value.text
                )
            )
            _loading.update { false }
        }
    }

    override fun onCleared() {
        super.onCleared()
        player.release()
    }
}
