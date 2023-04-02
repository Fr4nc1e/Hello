package com.francle.hello.feature.fullscreen.ui.presentation.event

import com.francle.hello.feature.home.domain.model.PostContentPair

sealed class FullScreenEvent {
    data class Navigate(val route: String) : FullScreenEvent()
    object NavigateUp : FullScreenEvent()
    data class DownloadMedia(val postContentPair: PostContentPair) : FullScreenEvent()
    object ShowDropMenu : FullScreenEvent()
}
