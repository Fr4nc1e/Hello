package com.francle.hello.feature.post.comment.ui.viewmodel

import android.content.SharedPreferences
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.francle.hello.R
import com.francle.hello.core.data.call.Resource
import com.francle.hello.core.data.file.toUri
import com.francle.hello.core.ui.event.UiEvent
import com.francle.hello.core.ui.util.TextState
import com.francle.hello.core.ui.util.UiText
import com.francle.hello.core.util.Constants
import com.francle.hello.feature.post.comment.domain.models.CommentType
import com.francle.hello.feature.post.comment.domain.repository.CommentRepository
import com.francle.hello.feature.post.comment.ui.event.CommentEvent
import com.mr0xf00.easycrop.CropResult
import com.mr0xf00.easycrop.ImageCropper
import com.mr0xf00.easycrop.crop
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CreateCommentViewModel @Inject constructor(
    private val commentRepository: CommentRepository,
    savedStateHandle: SavedStateHandle,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {
    private val _entityId = mutableStateOf("")

    private val _ownerUserId = mutableStateOf("")

    private val _commentType = MutableStateFlow<CommentType?>(null)
    val commentType = _commentType.asStateFlow()

    private val _profileImageUrl = MutableStateFlow("")
    val profileImageUrl = _profileImageUrl.asStateFlow()

    private val _inputComment = MutableStateFlow(TextState())
    val inputComment = _inputComment.asStateFlow()

    private val _chosenContentUriList = MutableStateFlow<List<Uri>?>(null)
    val chosenContentUriList = _chosenContentUriList.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _responseChannel = Channel<UiEvent>()
    val responseChannel = _responseChannel.receiveAsFlow()

    val imageCropper = ImageCropper()

    init {
        savedStateHandle.get<String>("entityId")?.also { entityId ->
            _entityId.value = entityId
        }

        savedStateHandle.get<String>("ownerUserId")?.also { ownerUserId ->
            _ownerUserId.value = ownerUserId
        }

        savedStateHandle.get<Int>("type")?.also { commentType ->
            _commentType.update {
                CommentType.values()[commentType]
            }
        }

        _profileImageUrl.update {
            sharedPreferences.getString(Constants.KEY_PROFILE_IMAGE_URL, null) ?: ""
        }
    }

    fun onEvent(event: CommentEvent) {
        when (event) {
            is CommentEvent.CreateComment -> {
                createComment(event.type)
            }
            is CommentEvent.InputComment -> {
                _inputComment.update {
                    it.copy(text = event.text)
                }
            }
            is CommentEvent.CropImage -> {
                viewModelScope.launch {
                    imageCropper.crop(event.uri, event.context).also { cropResult ->
                        when (cropResult) {
                            is CropResult.Success -> {
                                val newUri = cropResult.bitmap.asAndroidBitmap().toUri(
                                    event.context
                                )
                                _chosenContentUriList.update {
                                    it?.mapNotNull { uri ->
                                        if (uri == event.uri) {
                                            newUri
                                        } else {
                                            uri
                                        }
                                    }
                                }
                            }
                            else -> {}
                        }
                    }
                }
            }
            is CommentEvent.InputMediaContent -> {
                _chosenContentUriList.update {
                    event.uriList
                }
            }
        }
    }

    private fun createComment(type: CommentType) {
        viewModelScope.launch {
            _isLoading.update { true }
            if (
                _inputComment.value.text.isBlank() &&
                _chosenContentUriList.value.isNullOrEmpty()
            ) {
                _responseChannel.send(
                    UiEvent.Message(
                        UiText.StringResource(R.string.can_not_be_all_empty)
                    )
                )
                _isLoading.update { false }
                return@launch
            }
            commentRepository.createComment(
                arrowForwardUserId = _ownerUserId.value,
                arrowForwardEntityId = _entityId.value,
                arrowForwardEntityType = type.ordinal,
                commentText = _inputComment.value.text,
                commentMediaUris = _chosenContentUriList.value
            ).also { result ->
                when (result) {
                    is Resource.Error -> {
                        result.message?.let {
                            _responseChannel.send(UiEvent.Message(it))
                        }
                        _isLoading.update { false }
                    }
                    is Resource.Success -> {
                        _isLoading.update { false }
                        _responseChannel.send(UiEvent.NavigateUp)
                    }
                }
            }
        }
    }
}
