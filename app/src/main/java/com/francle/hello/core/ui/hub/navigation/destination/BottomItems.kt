package com.francle.hello.core.ui.hub.navigation.destination

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector
import com.francle.hello.R

enum class BottomItems(
    val route: String,
    val icon: ImageVector,
    @StringRes val contentDescription: Int
) {
    Home(
        route = Destination.Home.route,
        icon = Icons.Filled.Home,
        contentDescription = R.string.home
    )
}
