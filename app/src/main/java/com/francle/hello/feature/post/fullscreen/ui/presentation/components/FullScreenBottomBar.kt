package com.francle.hello.feature.post.fullscreen.ui.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.francle.hello.R

@Composable
fun FullScreenBottomBar(
    modifier: Modifier,
    likeState: Boolean,
    onCommentClick: () -> Unit,
    onRepostClick: () -> Unit,
    onLikeClick: () -> Unit,
    onShareClick: () -> Unit
) {
    BottomAppBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        IconButton(onClick = { onCommentClick() }) {
            Icon(
                imageVector = Icons.Filled.ChatBubbleOutline,
                contentDescription = stringResource(R.string.comment)
            )
        }

        // Repost
        IconButton(onClick = { onRepostClick() }) {
            Icon(
                imageVector = Icons.Filled.Repeat,
                contentDescription = stringResource(R.string.repost)
            )
        }

        // Like
        IconButton(onClick = { onLikeClick() }) {
            Icon(
                imageVector = Icons.Filled.FavoriteBorder,
                contentDescription = stringResource(R.string.like),
                tint = when (likeState) {
                    true -> {
                        Color.Red
                    }
                    false -> {
                        MaterialTheme.colorScheme.onPrimary
                    }
                }
            )
        }

        // Share
        IconButton(onClick = { onShareClick() }) {
            Icon(
                imageVector = Icons.Filled.Share,
                contentDescription = stringResource(R.string.share)
            )
        }
    }
}
