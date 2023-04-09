package com.francle.hello.feature.pair.ui.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import com.francle.hello.R
import com.francle.hello.core.ui.theme.ProfilePictureSizeMedium
import com.francle.hello.core.ui.theme.SpaceMedium
import com.francle.hello.core.ui.theme.SpaceSmall

@Composable
fun ActionButton(
    modifier: Modifier,
    onLikeClick: () -> Unit,
    onDislikeClick: () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(SpaceSmall)
    ) {
        Box(
            modifier = Modifier
                .size(ProfilePictureSizeMedium)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .clickable { onDislikeClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.dislike),
                contentDescription = null,
                modifier = Modifier.fillMaxSize().padding(SpaceMedium)
            )
        }
        Box(
            modifier = Modifier
                .size(ProfilePictureSizeMedium)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.tertiaryContainer)
                .clickable { onLikeClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.love),
                contentDescription = null,
                modifier = Modifier.fillMaxSize().padding(SpaceMedium)
            )
        }
    }
}
