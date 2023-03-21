package com.francle.hello.feature.login.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.francle.hello.core.data.util.AuthResult
import com.francle.hello.core.ui.util.TextState
import com.francle.hello.core.ui.util.Validator
import com.francle.hello.feature.home.event.LoginEvent
import com.francle.hello.feature.login.domain.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: LoginRepository
) : ViewModel() {
    private val _email = MutableStateFlow(TextState())
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow(TextState())
    val password = _password.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val resultChannel = Channel<AuthResult<Unit>>()
    val authResults = resultChannel.receiveAsFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.InputEmail -> {
                _email.update {
                    it.copy(text = event.emailText)
                }
            }
            is LoginEvent.InputPassword -> {
                _password.update {
                    it.copy(text = event.passwordText)
                }
            }
            LoginEvent.Login -> {
                login()
            }
        }
    }

    private fun login() {
        viewModelScope.launch {
            _loading.update { true }
            _email.update { it.copy(error = Validator.validateEmail(_email.value.text)) }
            _password.update { it.copy(error = Validator.validatePassword(_password.value.text)) }
            if (_email.value.error != null || _password.value.error != null) {
                _loading.update { false }
                return@launch
            }
            resultChannel.send(
                repository.login(
                    email = _email.value.text,
                    password = _password.value.text
                )
            )
            _loading.update { false }
        }
    }
}
