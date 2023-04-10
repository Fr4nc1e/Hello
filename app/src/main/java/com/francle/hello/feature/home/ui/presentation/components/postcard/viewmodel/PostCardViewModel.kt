package com.francle.hello.feature.home.ui.presentation.components.postcard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.francle.hello.core.data.call.Resource
import com.francle.hello.core.util.Constants
import com.francle.hello.feature.post.like.util.ForwardEntityType
import com.francle.hello.feature.home.ui.presentation.components.postcard.ui.event.PostCardEvent
import com.francle.hello.feature.post.like.data.request.LikeRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PostCardViewModel(
    private val injectionsProvider: InjectionsProvider,
    private val postId: String
) : ViewModel() {
    private val _likeState = MutableStateFlow(false)
    val likeState = _likeState.asStateFlow()

    private val likeRepository
        get() = injectionsProvider.likeRepository

    private val userId
        get() = injectionsProvider.sharedPreferences.getString(Constants.KEY_USER_ID, "") ?: ""

    fun onEvent(event: PostCardEvent) {
        when (event) {
            is PostCardEvent.ClickLikeButton -> {
                when (_likeState.value) {
                    false -> {
                        like(event.postUserId)
                    }
                    true -> {
                        dislike(event.postUserId)
                    }
                }
            }

            PostCardEvent.CheckLikeState -> {
                checkLikeState()
            }
        }
    }

    private fun dislike(postUserId: String) {
        viewModelScope.launch {
            likeRepository.dislike(
                arrowBackUserId = userId,
                arrowForwardUserId = postUserId,
                arrowForwardEntityId = postId,
                arrowForwardEntityType = ForwardEntityType.POST.ordinal
            ).also {
                when (it) {
                    is Resource.Error -> {
                        checkLikeState()
                    }
                    is Resource.Success -> {
                        _likeState.update { false }
                        checkLikeState()
                    }
                }
            }
        }
    }

    private fun like(postUserId: String) {
        viewModelScope.launch {
            likeRepository.like(
                LikeRequest(
                    arrowBackUserId = userId,
                    arrowForwardUserId = postUserId,
                    arrowForwardEntityId = postId,
                    arrowForwardEntityType = ForwardEntityType.POST.ordinal
                )
            ).also {
                when (it) {
                    is Resource.Error -> {
                        checkLikeState()
                    }
                    is Resource.Success -> {
                        _likeState.update { true }
                        checkLikeState()
                    }
                }
            }
        }
    }

    private fun checkLikeState() {
        viewModelScope.launch {
            likeRepository.checkLikeState(
                arrowBackUserId = userId,
                arrowForwardEntityId = postId
            ).also { result ->
                when (result) {
                    is Resource.Error -> {}
                    is Resource.Success -> {
                        when (result.data) {
                            true -> {
                                if (!_likeState.value) _likeState.update { true }
                            }
                            false -> {
                                if (_likeState.value) _likeState.update { false }
                            }
                            null -> {}
                        }
                    }
                }
            }
        }
    }
}
