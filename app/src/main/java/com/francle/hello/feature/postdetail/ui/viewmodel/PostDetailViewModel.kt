package com.francle.hello.feature.postdetail.ui.viewmodel

import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.francle.hello.R
import com.francle.hello.core.data.util.call.Resource
import com.francle.hello.core.ui.event.UiEvent
import com.francle.hello.core.ui.util.TextState
import com.francle.hello.core.ui.util.UiText
import com.francle.hello.core.util.Constants
import com.francle.hello.feature.home.domain.model.Post
import com.francle.hello.feature.home.domain.repository.PostRepository
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
    private val repository: PostRepository,
    savedStateHandle: SavedStateHandle,
    sharedPreferences: SharedPreferences
) : ViewModel() {
    private val userId = mutableStateOf("")

    private val _post = MutableStateFlow<Post?>(null)
    val post = _post.asStateFlow()

    private val _inputComment = MutableStateFlow(TextState())
    val inputComment = _inputComment.asStateFlow()

    private val _isOwnPost = MutableStateFlow(false)
    val isOwnPost = _isOwnPost.asStateFlow()

    private val _responseChannel = Channel<UiEvent>()
    val responseChannel = _responseChannel.receiveAsFlow()

    init {
        savedStateHandle.get<String>("postId")?.let {
            viewModelScope.launch {
                repository.getPost(it).collectLatest { result ->
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
                        }
                    }
                }
            }
        }
        userId.value = sharedPreferences.getString(Constants.KEY_USER_ID, "") ?: ""
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
                        repository.deletePostByPostId(it).let { result ->
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
        }
    }

    private fun onOwnPostJudge(postUserId: String): Boolean {
        return userId.value == postUserId
    }
}
