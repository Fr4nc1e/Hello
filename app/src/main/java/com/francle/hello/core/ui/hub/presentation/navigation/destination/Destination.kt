package com.francle.hello.core.ui.hub.presentation.navigation.destination

sealed class Destination(val route: String) {
    object Splash : Destination("splash")
    object Login : Destination("login")
    object Register : Destination("register")
    object Home : Destination("home")
    object PostDetail : Destination("post_detail")
    object FullScreenView : Destination("full_screen")
    object CreatePost : Destination("create_post")
    object Pair : Destination("pair")
    object Chat : Destination("chat")
    object Profile : Destination("profile")
    object Notification : Destination("notification")
    object Search : Destination("search")
}