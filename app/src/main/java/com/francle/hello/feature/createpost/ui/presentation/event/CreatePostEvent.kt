package com.francle.hello.feature.createpost.ui.presentation.event

import android.net.Uri

sealed class CreatePostEvent {
    data class InputPostText(val text: String) : CreatePostEvent()
    object CreatePost : CreatePostEvent()
    data class InputMediaContent(val uriList: List<Uri>?) : CreatePostEvent()
}
