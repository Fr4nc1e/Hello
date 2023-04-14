package com.francle.hello.feature.home.ui.presentation.event

import com.francle.hello.feature.home.domain.models.Post

sealed class HomeEvent {
    object Refresh : HomeEvent()

    object LoadNextItems : HomeEvent()

    object DeletePost : HomeEvent()

    object UpdateProfileUrl : HomeEvent()

    data class ClickMoreVert(val post: Post) : HomeEvent()

    data class IsOwnPost(val postUserId: String) : HomeEvent()
}
