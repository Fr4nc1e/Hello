package com.francle.hello.feature.home.ui.presentation.components.postcard.viewmodel

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun unifiedPostCardViewModel(postId: String): PostCardViewModel {
    val injectionsProvider = hiltViewModel<InjectionsProvider>()
    return viewModel(
        key = postId,
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return PostCardViewModel(
                    injectionsProvider,
                    postId
                ) as T
            }
        }
    )
}
