package com.francle.hello.feature.notification.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.francle.hello.core.ui.event.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

@HiltViewModel
class NotificationViewModel @Inject constructor() : ViewModel() {
    private val _responseChannel = Channel<UiEvent>()
    val responseChannel = _responseChannel.receiveAsFlow()
}
