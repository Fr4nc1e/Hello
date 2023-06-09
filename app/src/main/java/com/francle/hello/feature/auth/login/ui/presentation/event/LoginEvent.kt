package com.francle.hello.feature.auth.login.ui.presentation.event

sealed class LoginEvent {
    data class InputEmail(val emailText: String) : LoginEvent()
    data class InputPassword(val passwordText: String) : LoginEvent()
    object PasswordVisible : LoginEvent()
    object Login : LoginEvent()
}
