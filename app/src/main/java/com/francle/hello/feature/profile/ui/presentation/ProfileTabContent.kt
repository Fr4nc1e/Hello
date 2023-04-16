package com.francle.hello.feature.profile.ui.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

enum class ProfileTabContent(
    val tabName: String,
    val imageVector: ImageVector
) {
    Posts(
        tabName = "Posts",
        imageVector = Icons.Filled.Star
    ),
    Comments(
        tabName = "Comments",
        imageVector = Icons.Filled.Comment
    ),
    Likes(
        tabName = "Likes",
        imageVector = Icons.Filled.Favorite
    )
}
