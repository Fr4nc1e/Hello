package com.francle.hello.feature.profile.ui.event

sealed class ProfileEvent {
    object ClickMessage : ProfileEvent()
    object ClickMoreVert : ProfileEvent()
    object ClickLogOut : ProfileEvent()
    object ClickEdit : ProfileEvent()
    object LogOut : ProfileEvent()
}
