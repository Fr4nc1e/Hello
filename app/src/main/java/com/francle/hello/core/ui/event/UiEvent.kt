package com.francle.hello.core.ui.event

import com.francle.hello.core.ui.util.UiText

sealed class UiEvent {
    data class Navigate(val route: String) : UiEvent()
    object NavigateUp : UiEvent()
    data class Message(val message: UiText) : UiEvent()
    object LogOut : UiEvent()
}
