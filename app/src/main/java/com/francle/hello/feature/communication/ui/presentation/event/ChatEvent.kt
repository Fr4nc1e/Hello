package com.francle.hello.feature.communication.ui.presentation.event

sealed class ChatEvent {
    data class ClickMessageItem(val channelId: String) : ChatEvent()
    data class InputQuery(val query: String) : ChatEvent()
    object ClickHeaderMenu : ChatEvent()
}
