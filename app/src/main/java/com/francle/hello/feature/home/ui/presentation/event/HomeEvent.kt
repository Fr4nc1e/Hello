package com.francle.hello.feature.home.ui.presentation.event

import com.francle.hello.feature.home.domain.model.Post

sealed class HomeEvent {
    object Refresh : HomeEvent()

    object LoadNextItems : HomeEvent()

    data class ClickMoreVert(val post: Post) : HomeEvent()
}
