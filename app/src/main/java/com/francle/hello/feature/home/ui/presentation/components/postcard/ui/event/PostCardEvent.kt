package com.francle.hello.feature.home.ui.presentation.components.postcard.ui.event

sealed class PostCardEvent {
    data class ClickLikeButton(val postUserId: String) : PostCardEvent()
    object CheckLikeState : PostCardEvent()
}
