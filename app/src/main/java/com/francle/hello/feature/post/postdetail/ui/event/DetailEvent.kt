package com.francle.hello.feature.post.postdetail.ui.event

sealed class DetailEvent {
    data class InputComment(val text: String) : DetailEvent()
    data class IsOwnPost(val postUserId: String) : DetailEvent()
    object DeletePost : DetailEvent()
    data class ClickLikeButton(val type: Int) : DetailEvent()
    object CheckLikeState : DetailEvent()
}
