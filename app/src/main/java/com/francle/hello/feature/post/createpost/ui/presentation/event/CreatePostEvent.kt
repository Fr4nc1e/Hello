package com.francle.hello.feature.post.createpost.ui.presentation.event

import android.content.Context
import android.net.Uri

sealed class CreatePostEvent {
    data class InputPostText(val text: String) : CreatePostEvent()
    object CreatePost : CreatePostEvent()
    data class InputMediaContent(val uriList: List<Uri>?) : CreatePostEvent()
    data class CropImage(val uri: Uri, val context: Context) : CreatePostEvent()
}
