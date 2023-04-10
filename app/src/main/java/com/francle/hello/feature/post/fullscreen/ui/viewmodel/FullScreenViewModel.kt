package com.francle.hello.feature.post.fullscreen.ui.viewmodel

import android.content.SharedPreferences
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.francle.hello.R
import com.francle.hello.core.data.util.call.Resource
import com.francle.hello.core.data.util.download.Downloader
import com.francle.hello.core.ui.event.UiEvent
import com.francle.hello.core.ui.hub.navigation.util.fromJson
import com.francle.hello.core.ui.util.UiText
import com.francle.hello.core.util.Constants
import com.francle.hello.core.util.ForwardEntityType
import com.francle.hello.feature.post.fullscreen.ui.presentation.event.FullScreenEvent
import com.francle.hello.feature.home.domain.models.Post
import com.francle.hello.feature.post.like.data.request.LikeRequest
import com.francle.hello.feature.post.like.domain.repository.LikeRepository
import com.google.android.exoplayer2.Player
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class FullScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    val player: Player,
    private val downloader: Downloader,
    private val likeRepository: LikeRepository,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {
    private val userId
        get() = sharedPreferences.getString(Constants.KEY_USER_ID, "") ?: ""

    private val _post = MutableStateFlow<Post?>(null)
    val post = _post.asStateFlow()

    private val _index = MutableStateFlow<Int?>(null)
    val index = _index.asStateFlow()

    private val _likeState = MutableStateFlow(false)
    val likeState = _likeState.asStateFlow()

    private val _dropMenuVisible = MutableStateFlow(false)
    val dropMenuVisible = _dropMenuVisible.asStateFlow()

    private val _uiEventChannel = Channel<UiEvent>()
    val uiEventChannel = _uiEventChannel.receiveAsFlow()

    init {
        savedStateHandle.get<String>("post")?.let {
            val post = it.fromJson(Post::class.java)
            _post.update { post }
            checkLikeState()
        }
        savedStateHandle.get<Int>("index")?.let { i ->
            _index.update { i }
        }
    }

    fun onEvent(event: FullScreenEvent) {
        when (event) {
            is FullScreenEvent.DownloadMedia -> {
                viewModelScope.launch {
                    event.postContentPair.apply {
                        if (fileName != null && postContentUrl != null) {
                            downloader.downloadFile(
                                fileName = fileName,
                                uri = postContentUrl.toUri()
                            )
                            _dropMenuVisible.update { !it }
                        } else {
                            _uiEventChannel.send(
                                UiEvent.Message(
                                    message = UiText.StringResource(
                                        R.string.an_unknown_error_occurred
                                    )
                                )
                            )
                        }
                    }
                }
            }
            is FullScreenEvent.Navigate -> {
                viewModelScope.launch {
                    _uiEventChannel.send(UiEvent.Navigate(event.route))
                }
            }
            FullScreenEvent.NavigateUp -> {
                viewModelScope.launch {
                    _uiEventChannel.send(UiEvent.NavigateUp)
                    player.pause()
                }
            }
            FullScreenEvent.ShowDropMenu -> {
                _dropMenuVisible.update { !it }
            }
            FullScreenEvent.ClickLikeButton -> {
                when (_likeState.value) {
                    false -> {
                        like()
                    }
                    true -> {
                        dislike()
                    }
                }
            }
        }
    }

    private fun dislike() {
        viewModelScope.launch {
            _post.value?.also { post ->
                likeRepository.dislike(
                    arrowBackUserId = userId,
                    arrowForwardUserId = post.userId,
                    arrowForwardEntityId = post.id,
                    arrowForwardEntityType = ForwardEntityType.POST.ordinal
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

    private fun like() {
        viewModelScope.launch {
            _post.value?.also { post ->
                likeRepository.like(
                    LikeRequest(
                        arrowBackUserId = userId,
                        arrowForwardUserId = post.userId,
                        arrowForwardEntityId = post.id,
                        arrowForwardEntityType = ForwardEntityType.POST.ordinal
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

    override fun onCleared() {
        super.onCleared()
        player.release()
    }
}
