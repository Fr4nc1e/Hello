package com.francle.hello.core.ui.hub.viewmodel

import androidx.lifecycle.ViewModel
import com.francle.hello.core.ui.hub.presentation.navigation.destination.Destination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AppHubViewModel @Inject constructor() : ViewModel() {
    private val _curRoute = MutableStateFlow("")
    val curRoute = _curRoute.asStateFlow()

    private val showBottomBarList = listOf(
        Destination.Home.route,
        Destination.Chat.route,
        Destination.Search.route,
        Destination.Pair.route
    )

    fun getCurRoute(route: String) {
        _curRoute.update { route }
    }

    fun inList(): Boolean {
        return (_curRoute.value in showBottomBarList)
    }
}
