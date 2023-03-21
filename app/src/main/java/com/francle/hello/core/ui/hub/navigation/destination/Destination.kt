package com.francle.hello.core.ui.hub.navigation.destination

sealed class Destination(val route: String) {
    object Splash : Destination("splash")
    object Login : Destination("login")
    object Register : Destination("register")
    object Home : Destination("home")
}
