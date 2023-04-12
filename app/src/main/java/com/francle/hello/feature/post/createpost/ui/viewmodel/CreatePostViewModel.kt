package com.francle.hello.feature.post.createpost.ui.viewmodel

import android.content.SharedPreferences
import android.net.Uri
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.francle.hello.R
import com.francle.hello.core.data.call.Resource
import com.francle.hello.core.data.file.toUri
import com.francle.hello.core.ui.event.UiEvent
import com.francle.hello.core.ui.util.TextState
import com.francle.hello.core.ui.util.UiText
import com.francle.hello.core.util.Constants
import com.francle.hello.feature.post.createpost.domain.repository.CreatePostRepository
import com.francle.hello.feature.post.createpost.ui.presentation.event.CreatePostEvent
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
class CreatePostViewModel @Inject constructor(
    sharedPreferences: SharedPreferences,
    private val repository: CreatePostRepository
) : ViewModel() {
    private val _profileImageUrl = MutableStateFlow("")
    val profileImageUrl = _profileImageUrl.asStateFlow()

    private val _postText = MutableStateFlow(TextState())
    val postText = _postText.asStateFlow()

    private val _chosenContentUriList = MutableStateFlow<List<Uri>?>(null)
    val chosenContentUriList = _chosenContentUriList.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _resultChannel = Channel<UiEvent>()
    val resultChannel = _resultChannel.receiveAsFlow()

    val imageCropper = ImageCropper()

    init {
        _profileImageUrl.update {
            sharedPreferences.getString(Constants.PROFILE_IMAGE_URL, null) ?: ""
        }
    }

    fun onEvent(event: CreatePostEvent) {
        when (event) {
            is CreatePostEvent.InputPostText -> {
                _postText.update {
                    it.copy(text = event.text)
                }
            }
            CreatePostEvent.CreatePost -> {
                viewModelScope.launch {
                    if (
                        _postText.value.text.isBlank() &&
                        _chosenContentUriList.value.isNullOrEmpty()
                    ) {
                        _resultChannel.send(
                            UiEvent.Message(
                                UiText.StringResource(R.string.can_not_be_all_empty)
                            )
                        )
                        return@launch
                    }
                    _isLoading.update { true }
                    repository.createPost(
                        postText = _postText.value.text,
                        contentUriList = _chosenContentUriList.value
                    ).apply {
                        when (this) {
                            is Resource.Error -> {
                                message?.let {
                                    _resultChannel.send(UiEvent.Message(it))
                                }
                                _isLoading.update { false }
                            }
                            is Resource.Success -> {
                                message?.let {
                                    _resultChannel.send(UiEvent.Message(it))
                                }
                                _isLoading.update { false }
                                _resultChannel.send(UiEvent.NavigateUp)
                            }
                        }
                    }
                }
            }
            is CreatePostEvent.InputMediaContent -> {
                _chosenContentUriList.update {
                    event.uriList
                }
            }
            is CreatePostEvent.CropImage -> {
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
        }
    }
}
