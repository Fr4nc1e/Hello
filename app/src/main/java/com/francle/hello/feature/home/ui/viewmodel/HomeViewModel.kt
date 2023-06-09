package com.francle.hello.feature.home.ui.viewmodel

import android.content.SharedPreferences
import android.media.MediaMetadataRetriever
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.francle.hello.R
import com.francle.hello.core.data.call.Resource
import com.francle.hello.core.data.page.PagingManager
import com.francle.hello.core.ui.util.UiText
import com.francle.hello.core.util.Constants
import com.francle.hello.feature.home.domain.models.Post
import com.francle.hello.feature.home.domain.repository.PostRepository
import com.francle.hello.feature.home.ui.presentation.event.HomeEvent
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
    private val sharedPref: SharedPreferences,
    val retriever: MediaMetadataRetriever
) : ViewModel() {
    private val _userId = MutableStateFlow("")
    val userId = _userId.asStateFlow()

    private val _profileImageUrl = MutableStateFlow("")
    val profileImageUrl = _profileImageUrl.asStateFlow()

    private val _posts = MutableStateFlow(emptyList<Post?>())
    val posts = _posts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _isEndReach = MutableStateFlow(false)
    val isEndReach = _isEndReach.asStateFlow()

    private val _isOwnPost = MutableStateFlow(false)
    val isOwnPost = _isOwnPost.asStateFlow()

    private val _clickedMoreVert = MutableStateFlow<Post?>(null)
    val clickedMoreVert = _clickedMoreVert.asStateFlow()

    private val _responseChannel = Channel<UiText>()
    val responseChannel = _responseChannel.receiveAsFlow()

    private val _page = MutableStateFlow(0)

    private val pagingManager = PagingManager(
        initialPage = 0,
        onLoadUpdated = { loadingState ->
            _isLoading.update { loadingState }
        },
        onRequest = { nextPage ->
            postRepository.getHomePosts(
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
        sharedPref.apply {
            getString(Constants.KEY_USER_ID, "")?.let { userId ->
                _userId.update { userId }
            }
            getString(Constants.KEY_PROFILE_IMAGE_URL, "")?.let { profileImageUrl ->
                _profileImageUrl.update { profileImageUrl }
            }
        }
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            HomeEvent.Refresh -> {
                swipeRefresh()
            }

            HomeEvent.LoadNextItems -> {
                loadNextItems()
            }

            is HomeEvent.ClickMoreVert -> {
                _clickedMoreVert.update { event.post }
            }

            is HomeEvent.IsOwnPost -> {
                _isOwnPost.update { onOwnPostJudge(event.postUserId) }
            }

            HomeEvent.DeletePost -> {
                viewModelScope.launch {
                    _clickedMoreVert.value?.id?.let {
                        postRepository.deletePostByPostId(it).let { result ->
                            when (result) {
                                is Resource.Error -> {
                                    _responseChannel.send(
                                        result.message ?: UiText.StringResource(
                                            R.string.an_unknown_error_occurred
                                        )
                                    )
                                }
                                is Resource.Success -> {
                                    _posts.update { list ->
                                        list.filterNot { post ->
                                            post?.id == _clickedMoreVert.value?.id
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            HomeEvent.UpdateProfileUrl -> {
                updateProfileImageUrl()
            }
        }
    }

    private fun updateProfileImageUrl() {
        sharedPref.getString(Constants.KEY_PROFILE_IMAGE_URL, "")?.let { profileImageUrl ->
            _profileImageUrl.update { profileImageUrl }
        }
    }

    private fun onOwnPostJudge(postUserId: String): Boolean {
        return _userId.value == postUserId
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
    }
}
