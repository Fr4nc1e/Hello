package com.francle.hello.feature.register.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.francle.hello.core.data.util.AuthResult
import com.francle.hello.core.ui.util.TextState
import com.francle.hello.core.ui.util.Validator
import com.francle.hello.feature.register.domain.repository.RegisterRepository
import com.francle.hello.feature.register.event.RegisterEvent
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
    private val repository: RegisterRepository
) : ViewModel() {
    private val _email = MutableStateFlow(TextState())
    val email = _email.asStateFlow()

    private val _username = MutableStateFlow(TextState())
    val username = _username.asStateFlow()

    private val _password = MutableStateFlow(TextState())
    val password = _password.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _isPasswordVisible = MutableStateFlow(false)
    val isPasswordVisible = _isPasswordVisible.asStateFlow()

    private val resultChannel = Channel<AuthResult<String>>()
    val authResults = resultChannel.receiveAsFlow()

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
            _password.update { it.copy(error = Validator.validatePassword(_password.value.text)) }
            if (_email.value.error != null ||
                _username.value.error != null ||
                _password.value.error != null
            ) {
                _loading.update { false }
                return@launch
            }
            resultChannel.send(
                repository.register(
                    email = _email.value.text,
                    username = _username.value.text,
                    password = _password.value.text
                )
            )
            _loading.update { false }
        }
    }
}
