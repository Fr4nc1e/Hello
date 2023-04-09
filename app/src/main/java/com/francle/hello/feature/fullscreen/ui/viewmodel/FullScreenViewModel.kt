package com.francle.hello.feature.fullscreen.ui.viewmodel

import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.francle.hello.R
import com.francle.hello.core.data.util.download.Downloader
import com.francle.hello.core.ui.event.UiEvent
import com.francle.hello.core.ui.hub.navigation.util.fromJson
import com.francle.hello.core.ui.util.UiText
import com.francle.hello.feature.fullscreen.ui.presentation.event.FullScreenEvent
import com.francle.hello.feature.home.domain.model.Post
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
    private val downloader: Downloader
) : ViewModel() {
    private val _post = MutableStateFlow<Post?>(null)
    val post = _post.asStateFlow()

    private val _index = MutableStateFlow<Int?>(null)
    val index = _index.asStateFlow()

    private val _dropMenuVisible = MutableStateFlow(false)
    val dropMenuVisible = _dropMenuVisible.asStateFlow()

    private val _uiEventChannel = Channel<UiEvent>()
    val uiEventChannel = _uiEventChannel.receiveAsFlow()

    init {
        savedStateHandle.get<String>("post")?.let {
            val post = it.fromJson(Post::class.java)
            _post.update { post }
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
        }
    }

    override fun onCleared() {
        super.onCleared()
        player.release()
    }
}
