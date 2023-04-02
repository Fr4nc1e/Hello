package com.francle.hello.core.ui.hub.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.francle.hello.core.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AppHubViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : ViewModel() {
    private val _curRoute = MutableStateFlow("")
    val curRoute = _curRoute.asStateFlow()

    private val _profileImageUrl = MutableStateFlow("")
    val profileImageUrl = _profileImageUrl.asStateFlow()

    private val showBottomBarList = listOf("home")

    init {
        _profileImageUrl.update {
            sharedPreferences.getString(Constants.PROFILE_IMAGE_URL, null) ?: ""
        }
    }

    fun getCurRoute(route: String) {
        _curRoute.update { route }
    }

    fun inList(): Boolean {
        return (_curRoute.value in showBottomBarList)
    }
}
