package com.francle.hello.feature.profile.ui.event

sealed class ProfileEvent {
    object ClickMoreVert : ProfileEvent()
    object ClickLogOut : ProfileEvent()
    object LogOut : ProfileEvent()
}
