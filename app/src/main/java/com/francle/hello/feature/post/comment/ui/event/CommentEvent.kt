package com.francle.hello.feature.post.comment.ui.event

import android.content.Context
import android.net.Uri
import com.francle.hello.feature.post.comment.domain.models.CommentType

sealed class CommentEvent {
    data class InputComment(val text: String) : CommentEvent()
    data class CreateComment(val type: CommentType) : CommentEvent()
    data class InputMediaContent(val uriList: List<Uri>?) : CommentEvent()
    data class CropImage(val uri: Uri, val context: Context) : CommentEvent()
}
