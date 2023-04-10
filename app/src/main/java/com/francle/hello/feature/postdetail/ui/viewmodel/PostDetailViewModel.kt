package com.francle.hello.feature.postdetail.ui.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.francle.hello.R
import com.francle.hello.core.data.util.call.Resource
import com.francle.hello.core.ui.event.UiEvent
import com.francle.hello.core.ui.util.TextState
import com.francle.hello.core.ui.util.UiText
import com.francle.hello.core.util.Constants
import com.francle.hello.core.util.ForwardEntityType
import com.francle.hello.feature.home.domain.models.Post
import com.francle.hello.feature.home.domain.repository.PostRepository
import com.francle.hello.feature.like.data.request.LikeRequest
import com.francle.hello.feature.like.domain.repository.LikeRepository
import com.francle.hello.feature.postdetail.ui.event.DetailEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val likeRepository: LikeRepository,
    savedStateHandle: SavedStateHandle,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {
    private val userId
        get() = sharedPreferences.getString(Constants.KEY_USER_ID, "") ?: ""

    private val _post = MutableStateFlow<Post?>(null)
    val post = _post.asStateFlow()

    private val _likeState = MutableStateFlow(false)
    val likeState = _likeState.asStateFlow()

    private val _inputComment = MutableStateFlow(TextState())
    val inputComment = _inputComment.asStateFlow()

    private val _isOwnPost = MutableStateFlow(false)
    val isOwnPost = _isOwnPost.asStateFlow()

    private val _responseChannel = Channel<UiEvent>()
    val responseChannel = _responseChannel.receiveAsFlow()

    init {
        savedStateHandle.get<String>("postId")?.let {
            viewModelScope.launch {
                postRepository.getPost(it).collectLatest { result ->
                    when (result) {
                        is Resource.Error -> {
                            _responseChannel.send(
                                UiEvent.Message(
                                    result.message ?: UiText.StringResource(
                                        R.string.an_unknown_error_occurred
                                    )
                                )
                            )
                        }
                        is Resource.Success -> {
                            _post.update { result.data }
                            checkLikeState()
                        }
                    }
                }
            }
        }
    }

    fun onEvent(event: DetailEvent) {
        when (event) {
            is DetailEvent.InputComment -> {
                _inputComment.update {
                    it.copy(text = event.text)
                }
            }
            is DetailEvent.IsOwnPost -> {
                _isOwnPost.update { onOwnPostJudge(event.postUserId) }
            }
            DetailEvent.DeletePost -> {
                viewModelScope.launch {
                    _post.value?.id?.let {
                        postRepository.deletePostByPostId(it).let { result ->
                            when (result) {
                                is Resource.Error -> {
                                    _responseChannel.send(
                                        UiEvent.Message(
                                            result.message ?: UiText.StringResource(
                                                R.string.an_unknown_error_occurred
                                            )
                                        )
                                    )
                                }
                                is Resource.Success -> {
                                    _responseChannel.send(UiEvent.NavigateUp)
                                }
                            }
                        }
                    }
                }
            }
            is DetailEvent.ClickLikeButton -> {
                when (_likeState.value) {
                    false -> {
                        like(event.type)
                    }
                    true -> {
                        dislike(event.type)
                    }
                }
            }

            DetailEvent.CheckLikeState -> {
                checkLikeState()
            }
        }
    }

    private fun onOwnPostJudge(postUserId: String): Boolean {
        return userId == postUserId
    }

    private fun dislike(type: Int) {
        viewModelScope.launch {
            _post.value?.also { post ->
                likeRepository.dislike(
                    arrowBackUserId = userId,
                    arrowForwardUserId = post.userId,
                    arrowForwardEntityId = post.id,
                    arrowForwardEntityType = ForwardEntityType.values()[type].ordinal
                ).also { result ->
                    when (result) {
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
    }

    private fun like(type: Int) {
        viewModelScope.launch {
            _post.value?.also { post ->
                likeRepository.like(
                    LikeRequest(
                        arrowBackUserId = userId,
                        arrowForwardUserId = post.userId,
                        arrowForwardEntityId = post.id,
                        arrowForwardEntityType = ForwardEntityType.values()[type].ordinal
                    )
                ).also { result ->
                    when (result) {
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
    }

    private fun checkLikeState() {
        viewModelScope.launch {
            _post.value?.also { post ->
                likeRepository.checkLikeState(
                    arrowBackUserId = userId,
                    arrowForwardEntityId = post.id
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
}
