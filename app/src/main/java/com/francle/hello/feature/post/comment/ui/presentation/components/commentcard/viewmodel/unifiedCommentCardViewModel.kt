package com.francle.hello.feature.post.comment.ui.presentation.components.commentcard.viewmodel

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun unifiedCommentCardViewModel(commentId: String): CommentCardViewModel {
    val commentCardInjectionsProvider = hiltViewModel<CommentCardInjectionsProvider>()
    return viewModel(
        key = commentId,
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return CommentCardViewModel(
                    commentCardInjectionsProvider,
                    commentId
                ) as T
            }
        }
    )
}
