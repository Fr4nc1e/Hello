package com.francle.hello.core.ui.hub.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.francle.hello.core.ui.hub.presentation.navigation.destination.Destination
import com.francle.hello.core.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import io.getstream.chat.android.client.ChatClient
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class AppHubViewModel @Inject constructor(
    client: ChatClient,
    sharedPreferences: SharedPreferences
) : ViewModel() {
    private val _curRoute = MutableStateFlow("")
    val curRoute = _curRoute.asStateFlow()

    private val showBottomBarList = listOf(
        Destination.Home.route,
        Destination.Chat.route,
        Destination.Search.route,
        Destination.Pair.route
    )

    init {
        client.connectUser(
            user = io.getstream.chat.android.client.models.User(
                id = sharedPreferences.getString(Constants.KEY_USER_ID, "") ?: "",
                name = sharedPreferences.getString(Constants.KEY_USER_NAME, "") ?: "",
                image = sharedPreferences.getString(Constants.KEY_PROFILE_IMAGE_URL, "") ?: ""
            ),
            token = sharedPreferences.getString(Constants.KEY_STREAM_TOKEN, "") ?: ""
        ).enqueue()
    }

    fun getCurRoute(route: String) {
        _curRoute.update { route }
    }

    fun inList(): Boolean {
        return (_curRoute.value in showBottomBarList)
    }
}
