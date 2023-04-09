package com.francle.hello.feature.pair.ui.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.francle.hello.R
import com.francle.hello.core.data.util.call.Resource
import com.francle.hello.core.ui.event.UiEvent
import com.francle.hello.core.ui.util.UiText
import com.francle.hello.core.util.Constants
import com.francle.hello.feature.pair.domain.models.PairUser
import com.francle.hello.feature.pair.domain.repository.PairRepository
import com.francle.hello.feature.pair.ui.event.PairEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class PairViewModel @Inject constructor(
    sharedPreferences: SharedPreferences,
    private val repository: PairRepository
) : ViewModel() {
    private val _pairUser = MutableStateFlow<PairUser?>(null)
    val pairUser = _pairUser.asStateFlow()

    private val _profileImageUrl = MutableStateFlow("")
    val profileImageUrl = _profileImageUrl.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _resultChannel = Channel<UiEvent>()
    val resultChannel = _resultChannel.receiveAsFlow()

    init {
        _profileImageUrl.update {
            sharedPreferences.getString(Constants.PROFILE_IMAGE_URL, null) ?: ""
        }
        getPairUser()
    }

    fun onEvent(event: PairEvent) {
        when (event) {
            PairEvent.ClickDislike -> {}
            PairEvent.ClickLike -> {}
            PairEvent.Pair -> {
                getPairUser()
            }
        }
    }

    private fun getPairUser() {
        viewModelScope.launch {
            _loading.update { true }
            repository.getPairUser().collect { result ->
                when (result) {
                    is Resource.Error -> {
                        result.message?.let {
                            _resultChannel.send(UiEvent.Message(it))
                        }
                        _loading.update { false }
                    }
                    is Resource.Success -> {
                        result.data?.let { user ->
                            _pairUser.update { user }
                        } ?: kotlin.run {
                            _resultChannel.send(
                                UiEvent.Message(
                                    UiText.StringResource(R.string.no_more_user_to_pair)
                                )
                            )
                        }
                        _loading.update { false }
                    }
                }
            }
        }
    }
}
