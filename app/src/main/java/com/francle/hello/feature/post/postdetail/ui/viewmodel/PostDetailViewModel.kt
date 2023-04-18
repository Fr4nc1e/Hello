package com.francle.hello.feature.post.postdetail.ui.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.francle.hello.R
import com.francle.hello.core.data.call.Resource
import com.francle.hello.core.data.page.PagingManager
import com.francle.hello.core.ui.event.UiEvent
import com.francle.hello.core.ui.util.UiText
import com.francle.hello.core.util.Constants
import com.francle.hello.feature.home.domain.models.Post
import com.francle.hello.feature.home.domain.repository.PostRepository
import com.francle.hello.feature.post.comment.domain.models.Comment
import com.francle.hello.feature.post.comment.domain.repository.CommentRepository
import com.francle.hello.feature.post.like.data.request.LikeRequest
import com.francle.hello.feature.post.like.domain.repository.LikeRepository
import com.francle.hello.feature.post.like.util.ForwardEntityType
import com.francle.hello.feature.post.postdetail.ui.event.DetailEvent
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
    private val commentRepository: CommentRepository,
    savedStateHandle: SavedStateHandle,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {
    private val userId
        get() = sharedPreferences.getString(Constants.KEY_USER_ID, "") ?: ""

    private val _post = MutableStateFlow<Post?>(null)
    val post = _post.asStateFlow()
    
    private val _comments = MutableStateFlow(emptyList<Comment?>())
    val comments = _comments.asStateFlow()

    private val _likeState = MutableStateFlow(false)
    val likeState = _likeState.asStateFlow()

    private val _isOwnPost = MutableStateFlow(false)
    val isOwnPost = _isOwnPost.asStateFlow()

    private val _isOwnComment = MutableStateFlow(false)
    val isOwnComment = _isOwnComment.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isEndReach = MutableStateFlow(false)
    val isEndReach = _isEndReach.asStateFlow()

    private val _responseChannel = Channel<UiEvent>()
    val responseChannel = _responseChannel.receiveAsFlow()

    private val _page = MutableStateFlow(0)

    private val pagingManager = PagingManager(
        initialPage = 0,
        onLoadUpdated = { loadingState ->
            _isLoading.update { loadingState }
        },
        onRequest = { nextPage ->
            commentRepository.getCommentsOfEntity(
                entityId = _post.value?.id ?: "",
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
                                UiEvent.Message(
                                    result.message ?: UiText.StringResource(
                                        R.string.an_unexpected_error_occurred
                                    )
                                )
                            )
                    }
                    is Resource.Success -> {
                        result.data?.let { comments ->
                            if (comments.isEmpty()) {
                                _isEndReach.update { true }
                                return@collect
                            } else { _isEndReach.update { false } }
                            _comments.update { 
                                it + comments 
                            }
                            _page.update { it + 1 }
                        }
                    }
                }
            }
        }
    )

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
                            loadNextItems()
                        }
                    }
                }
            }
        }
    }

    fun onEvent(event: DetailEvent) {
        when (event) {
            is DetailEvent.IsOwnPost -> {
                _isOwnPost.update { onOwnEntityJudge(event.postUserId) }
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
                        like(event.type.ordinal)
                    }
                    true -> {
                        dislike(event.type.ordinal)
                    }
                }
            }

            DetailEvent.CheckLikeState -> {
                checkLikeState()
            }

            is DetailEvent.IsOwnComment -> {
                _isOwnComment.update {
                    onOwnEntityJudge(event.commentUserId)
                }
            }

            DetailEvent.LoadItems -> {
                loadNextItems()
            }

            is DetailEvent.Navigate -> {
                viewModelScope.launch {
                    _responseChannel.send(UiEvent.Navigate(event.route))
                }
            }
        }
    }

    private fun loadNextItems() {
        viewModelScope.launch {
            pagingManager.currentPage = _page.value
            pagingManager.loadNextItems()
        }
    }

    private fun onOwnEntityJudge(entityUserId: String): Boolean {
        return userId == entityUserId
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
