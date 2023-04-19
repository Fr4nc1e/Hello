package com.francle.hello.core.ui.hub.presentation.navigation.destination

sealed class Destination(val route: String) {
    object Splash : Destination("splash")
    object Login : Destination("login")
    object Register : Destination("register")
    object Home : Destination("home")
    object PostDetail : Destination("post_detail")
    object FullScreenView : Destination("full_screen")
    object CreatePost : Destination("create_post")
    object CreateComment : Destination("create_comment")
    object Pair : Destination("pair")
    object Chat : Destination("chat")
    object Profile : Destination("profile")
    object EditProfile : Destination("edit_profile")
    object Notification : Destination("notification")
    object Search : Destination("search")
}
