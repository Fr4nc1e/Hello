package com.francle.hello.feature.register.ui.presentation.event

sealed class RegisterEvent {
    data class InputEmail(val emailText: String) : RegisterEvent()
    data class InputUsername(val usernameText: String) : RegisterEvent()
    data class InputPassword(val passwordText: String) : RegisterEvent()
    object PasswordVisible : RegisterEvent()
    object Register : RegisterEvent()
}
