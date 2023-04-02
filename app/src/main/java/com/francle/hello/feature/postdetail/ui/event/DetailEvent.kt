package com.francle.hello.feature.postdetail.ui.event

sealed class DetailEvent {
    data class InputComment(val text: String) : DetailEvent()
    data class IsOwnPost(val postUserId: String) : DetailEvent()
    object DeletePost : DetailEvent()
}
