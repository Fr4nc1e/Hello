package com.francle.hello.feature.post.postdetail.ui.event

import com.francle.hello.feature.post.like.util.ForwardEntityType

sealed class DetailEvent {
    data class IsOwnPost(val postUserId: String) : DetailEvent()
    data class IsOwnComment(val commentUserId: String) : DetailEvent()
    object DeletePost : DetailEvent()
    data class ClickLikeButton(val type: ForwardEntityType) : DetailEvent()
    object CheckLikeState : DetailEvent()
    object LoadItems : DetailEvent()
    data class Navigate(val route: String) : DetailEvent()
}
