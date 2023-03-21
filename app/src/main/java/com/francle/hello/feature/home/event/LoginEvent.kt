package com.francle.hello.feature.home.event

sealed class LoginEvent {
    data class InputEmail(val emailText: String) : LoginEvent()
    data class InputPassword(val passwordText: String) : LoginEvent()
    object Login : LoginEvent()
}
