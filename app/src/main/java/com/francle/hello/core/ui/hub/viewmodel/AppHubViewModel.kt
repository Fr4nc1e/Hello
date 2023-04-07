package com.francle.hello.core.ui.hub.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AppHubViewModel @Inject constructor() : ViewModel() {
    private val _curRoute = MutableStateFlow("")
    val curRoute = _curRoute.asStateFlow()

    private val showBottomBarList = listOf("home", "chat", "profile", "pair")

    fun getCurRoute(route: String) {
        _curRoute.update { route }
    }

    fun inList(): Boolean {
        return (_curRoute.value in showBottomBarList)
    }
}
