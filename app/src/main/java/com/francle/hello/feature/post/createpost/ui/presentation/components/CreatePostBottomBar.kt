package com.francle.hello.feature.post.createpost.ui.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.PhotoAlbum
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.francle.hello.R

@Composable
fun CreatePostBottomBar(
    modifier: Modifier,
    onMediaClick: () -> Unit,
    onCameraClick: () -> Unit
) {
    BottomAppBar(
        modifier = modifier
    ) {
        IconButton(onClick = { onMediaClick() }) {
            Icon(
                imageVector = Icons.Filled.PhotoAlbum,
                contentDescription = stringResource(R.string.photo)
            )
        }

        // Repost
        IconButton(onClick = { onCameraClick() }) {
            Icon(
                imageVector = Icons.Filled.Camera,
                contentDescription = stringResource(R.string.camera)
            )
        }
    }
}
