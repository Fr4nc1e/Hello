package com.francle.hello.feature.home.ui.viewmodel

import android.content.SharedPreferences
import android.media.MediaMetadataRetriever
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.francle.hello.R
import com.francle.hello.core.data.util.Resource
import com.francle.hello.core.data.util.page.PagingManager
import com.francle.hello.core.ui.util.UiText
import com.francle.hello.core.util.Constants
import com.francle.hello.feature.home.domain.model.Post
import com.francle.hello.feature.home.domain.model.PostContentPair
import com.francle.hello.feature.home.domain.repository.PostRepository
import com.francle.hello.feature.home.ui.presentation.event.HomeEvent
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
class HomeViewModel @Inject constructor(
    private val postRepository: PostRepository,
    sharedPref: SharedPreferences,
    val retriever: MediaMetadataRetriever,
    val player: Player
) : ViewModel() {
    private val _posts = MutableStateFlow(emptyList<Post?>())
    val posts = _posts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _isEndReach = MutableStateFlow(false)
    val isEndReach = _isEndReach.asStateFlow()

    private val _mediaItems = MutableStateFlow(emptyList<PostContentPair>())
    val mediaItems = _mediaItems.asStateFlow()

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex = _currentIndex.asStateFlow()

    private val _isMediaItemClicked = MutableStateFlow(false)
    val isMediaItemClicked = _isMediaItemClicked.asStateFlow()

    private val _responseChannel = Channel<UiText>()
    val responseChannel = _responseChannel.receiveAsFlow()

    private val _page = MutableStateFlow(0)

    private val pagingManager = PagingManager(
        initialPage = 0,
        onLoadUpdated = { loadingState ->
            _isLoading.update { loadingState }
        },
        onRequest = { nextPage ->
            postRepository.getPosts(
                userId = sharedPref.getString(Constants.KEY_USER_ID, "") ?: "",
                page = nextPage,
                pageSize = Constants.PAGE_SIZE
            )
        },
        onSuccess = { items ->
            items.collect { result ->
                when (result) {
                    is Resource.Error -> {
                        _responseChannel
                            .send(
                                result.message ?: UiText.StringResource(
                                    R.string.an_unexpected_error_occurred
                                )
                            )
                    }
                    is Resource.Success -> {
                        result.data?.let { posts ->
                            if (posts.isEmpty()) {
                                _isEndReach.update { true }
                                _responseChannel.send(UiText.StringResource(R.string.no_more_posts))
                                return@collect
                            } else { _isEndReach.update { false } }
                            _posts.update { it + posts }
                            _page.update { it + 1 }
                        }
                    }
                }
            }
        }
    )

    init {
        loadNextItems()
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.ClickMediaItem -> {
                _isMediaItemClicked.update { true }
                _mediaItems.update { event.postContentPairs }
                _currentIndex.update { event.currentIndex }
            }

            HomeEvent.DisMissFullScreen -> {
                _isMediaItemClicked.update { false }
            }

            HomeEvent.Refresh -> {
                swipeRefresh()
            }

            HomeEvent.LoadNextItems -> {
                loadNextItems()
            }
        }
    }
    private fun loadNextItems() {
        viewModelScope.launch {
            pagingManager.currentPage = _page.value
            pagingManager.loadNextItems()
        }
    }

    private fun swipeRefresh() {
        _isRefreshing.update { true }
        _page.update { 0 }
        _posts.update { emptyList() }
        loadNextItems()
        _isRefreshing.update { false }
    }

    override fun onCleared() {
        super.onCleared()
        retriever.release()
        player.release()
    }
}
