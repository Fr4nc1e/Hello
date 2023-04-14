package com.francle.hello.feature.communication.ui.presentation.event

sealed class ChatEvent {
    data class ClickMessageItem(val channelId: String) : ChatEvent()
}
