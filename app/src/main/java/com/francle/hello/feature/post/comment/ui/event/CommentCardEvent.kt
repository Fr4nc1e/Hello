package com.francle.hello.feature.post.comment.ui.event

sealed class CommentCardEvent {
    data class ClickLikeButton(val commentUserId: String) : CommentCardEvent()
    object CheckLikeState : CommentCardEvent()
}
