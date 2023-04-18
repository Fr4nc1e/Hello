package com.francle.hello.feature.post.comment.ui.presentation.components.commentcard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.francle.hello.core.data.call.Resource
import com.francle.hello.core.util.Constants
import com.francle.hello.feature.post.comment.ui.event.CommentCardEvent
import com.francle.hello.feature.post.like.data.request.LikeRequest
import com.francle.hello.feature.post.like.util.ForwardEntityType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CommentCardViewModel(
    private val commentCardInjectionsProvider: CommentCardInjectionsProvider,
    private val commentId: String
) : ViewModel() {
    private val _likeState = MutableStateFlow(false)
    val likeState = _likeState.asStateFlow()

    private val likeRepository
        get() = commentCardInjectionsProvider.likeRepository

    private val userId
        get() = commentCardInjectionsProvider.sharedPreferences.getString(Constants.KEY_USER_ID, "") ?: ""

    fun onEvent(event: CommentCardEvent) {
        when (event) {
            CommentCardEvent.CheckLikeState -> {
                checkLikeState()
            }
            is CommentCardEvent.ClickLikeButton -> {
                when (_likeState.value) {
                    false -> {
                        like(event.commentUserId)
                    }
                    true -> {
                        dislike(event.commentUserId)
                    }
                }
            }
        }
    }

    private fun dislike(commentUserId: String) {
        viewModelScope.launch {
            likeRepository.dislike(
                arrowBackUserId = userId,
                arrowForwardUserId = commentUserId,
                arrowForwardEntityId = commentId,
                arrowForwardEntityType = ForwardEntityType.COMMENT.ordinal
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

    private fun like(commentUserId: String) {
        viewModelScope.launch {
            likeRepository.like(
                LikeRequest(
                    arrowBackUserId = userId,
                    arrowForwardUserId = commentUserId,
                    arrowForwardEntityId = commentId,
                    arrowForwardEntityType = ForwardEntityType.COMMENT.ordinal
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
                arrowForwardEntityId = commentId
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
