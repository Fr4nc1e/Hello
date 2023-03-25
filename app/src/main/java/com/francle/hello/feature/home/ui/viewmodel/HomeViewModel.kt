package com.francle.hello.feature.home.ui.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.francle.hello.R
import com.francle.hello.core.data.util.Resource
import com.francle.hello.core.data.util.page.PagingManager
import com.francle.hello.core.ui.util.UiText
import com.francle.hello.core.util.Constants
import com.francle.hello.feature.home.domain.model.Post
import com.francle.hello.feature.home.domain.repository.PostRepository
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
    sharedPref: SharedPreferences
) : ViewModel() {
    private val _posts = MutableStateFlow(emptyList<Post?>())
    val posts = _posts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _responseChannel = Channel<UiText>()
    val responseChannel = _responseChannel.receiveAsFlow()

    private val _page = MutableStateFlow(0)

    private val pagingManager = PagingManager(
        initialPage = _page.value,
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
                                _responseChannel.send(UiText.StringResource(R.string.no_more_posts))
                                return@collect
                            }
                            _posts.update { it + posts }
                            _page.update {
                                val nextPage = it + 1
                                nextPage
                            }
                        }
                    }
                }
            }
        }
    )

    init {
        loadNextItems()
    }

    private fun loadNextItems() {
        viewModelScope.launch {
            pagingManager.currentPage = _page.value
            pagingManager.loadNextItems()
        }
    }
}
