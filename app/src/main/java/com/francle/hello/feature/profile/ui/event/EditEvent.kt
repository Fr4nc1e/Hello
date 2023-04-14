package com.francle.hello.feature.profile.ui.event

import android.content.Context
import android.net.Uri

sealed class EditEvent {
    data class EditUserName(val username: String) : EditEvent()
    data class EditAge(val age: String) : EditEvent()
    data class EditBio(val bio: String) : EditEvent()
    data class EditProfileImage(val profileImageUri: Uri) : EditEvent()
    data class EditBannerImage(val bannerImageUri: Uri) : EditEvent()

    // 0 -> profileImage, 1 -> bannerImage
    data class CropImage(
        val uri: Uri,
        val type: Int,
        val context: Context
    ) : EditEvent()
    object EditCompleted : EditEvent()
}
