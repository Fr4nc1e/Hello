package com.francle.hello.feature.home.ui.presentation.event

import com.francle.hello.feature.home.domain.model.PostContentPair

sealed class HomeEvent {
    data class ClickMediaItem(
        val postContentPairs: List<PostContentPair>,
        val currentIndex: Int
    ) : HomeEvent()

    object DisMissFullScreen : HomeEvent()
}
