package com.francle.hello.core.ui.hub.presentation.navigation.destination

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TagFaces
import androidx.compose.ui.graphics.vector.ImageVector
import com.francle.hello.R

enum class BottomItems(
    val route: String,
    val icon: ImageVector,
    @StringRes val contentDescription: Int
) {
    Pair(
        route = Destination.Pair.route,
        icon = Icons.Filled.TagFaces,
        contentDescription = R.string.pair
    ),
    Home(
        route = Destination.Home.route,
        icon = Icons.Filled.Home,
        contentDescription = R.string.home
    ),
    Chat(
        route = Destination.Chat.route,
        icon = Icons.Filled.Chat,
        contentDescription = R.string.chat
    ),
    Profile(
        route = Destination.Profile.route,
        icon = Icons.Filled.Person,
        contentDescription = R.string.profile
    )
}
