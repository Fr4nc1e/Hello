package com.francle.hello.feature.profile.ui.viewmodel

import android.content.SharedPreferences
import android.net.Uri
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.francle.hello.R
import com.francle.hello.core.data.call.Resource
import com.francle.hello.core.data.file.toUri
import com.francle.hello.core.ui.event.UiEvent
import com.francle.hello.core.ui.util.UiText
import com.francle.hello.core.util.Constants
import com.francle.hello.feature.profile.domain.repository.ProfileRepository
import com.francle.hello.feature.profile.ui.event.EditEvent
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
class EditProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {
    private val _username = MutableStateFlow("")
    val username = _username.asStateFlow()
    
    private val _age = MutableStateFlow<Int?>(null)
    val age = _age.asStateFlow()
    
    private val _bio = MutableStateFlow<String?>(null)
    val bio = _bio.asStateFlow()

    private val _bannerImageUrl = MutableStateFlow<String?>(null)
    val bannerImageUrl = _bannerImageUrl.asStateFlow()

    private val _profileImageUrl = MutableStateFlow<String?>(null)
    val profileImageUrl = _profileImageUrl.asStateFlow()

    private val _chosenBannerImageUri = MutableStateFlow<Uri?>(null)
    val chosenBannerImageUri = _chosenBannerImageUri.asStateFlow()

    private val _chosenProfileImageUri = MutableStateFlow<Uri?>(null)
    val chosenProfileImageUri = _chosenProfileImageUri.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _resultChannel = Channel<UiEvent>()
    val resultChannel = _resultChannel.receiveAsFlow()

    val imageCropper = ImageCropper()

    init {
        val userId = sharedPreferences.getString(Constants.KEY_USER_ID, "") ?: ""
        viewModelScope.launch {
            profileRepository.getUserProfile(userId).collect { result ->
                when (result) {
                    is Resource.Error -> {
                        _resultChannel.send(
                            UiEvent.Message(
                                result.message ?: UiText.StringResource(
                                    R.string.an_unknown_error_occurred
                                )
                            )
                        )
                    }
                    is Resource.Success -> {
                        result.data?.also { user -> 
                            _username.update { user.username }
                            _age.update { user.age }
                            _bio.update { user.bio }
                            _profileImageUrl.update { user.profileImageUrl }
                            _bannerImageUrl.update { user.bannerImageUrl }
                        }
                    }
                }
            }
        }
    }
    
    fun onEvent(event: EditEvent) {
        when (event) {
            is EditEvent.EditUserName -> {
                _username.update { event.username }
            }
            is EditEvent.EditAge -> {
                _age.update { event.age.toInt() }
            }
            is EditEvent.EditBannerImage -> {
                _chosenBannerImageUri.update { event.bannerImageUri }
            }
            is EditEvent.EditProfileImage -> {
                _chosenProfileImageUri.update { event.profileImageUri }
            }
            is EditEvent.EditBio -> {
                _bio.update { event.bio }
            }
            EditEvent.EditCompleted -> {
                completeEdit()
            }

            is EditEvent.CropImage -> {
                viewModelScope.launch {
                    imageCropper.crop(event.uri, event.context).also { cropResult ->
                        when (cropResult) {
                            is CropResult.Success -> {
                                val newUri = cropResult.bitmap.asAndroidBitmap().toUri(
                                    event.context
                                )
                                when (event.type) {
                                    0 -> {
                                        _chosenProfileImageUri.update { newUri }
                                    }
                                    1 -> {
                                        _chosenBannerImageUri.update { newUri }
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

    private fun completeEdit() {
        viewModelScope.launch {
            _loading.update { true }
            profileRepository.editProfile(
                username = _username.value,
                age = _age.value,
                bio = _bio.value,
                profileImageUri = _chosenProfileImageUri.value,
                bannerImageUri = _chosenBannerImageUri.value
            ).also { result ->
                when (result) {
                    is Resource.Error -> {
                        _resultChannel.send(
                            UiEvent.Message(
                                result.message ?: UiText.StringResource(
                                    R.string.an_unknown_error_occurred
                                )
                            )
                        )
                    }
                    is Resource.Success -> {
                        result.data?.also { editProfileResponse ->
                            editProfileResponse
                                .apply {
                                    profileImageUrl?.also {
                                        sharedPreferences.edit().putString(
                                            Constants.KEY_PROFILE_IMAGE_URL,
                                            it
                                        ).apply()
                                    }
                                }
                        }
                        _resultChannel.send(UiEvent.NavigateUp)
                    }
                }
            }
            _loading.update { false }
        }
    }
}
