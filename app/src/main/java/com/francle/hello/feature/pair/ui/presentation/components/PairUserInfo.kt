package com.francle.hello.feature.pair.ui.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.francle.hello.R
import com.francle.hello.core.ui.theme.ProfilePictureSizeMedium
import com.francle.hello.core.ui.theme.SpaceMedium

@Composable
fun PairUserInfo(
    modifier: Modifier,
    profileImageUrl: String?,
    username: String?,
    age: Int?
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(SpaceMedium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current)
                    .data(profileImageUrl)
                    .crossfade(true)
                    .build()
            ),
            contentDescription = stringResource(R.string.profile_image),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(ProfilePictureSizeMedium)
                .clip(CircleShape)
        )

        Text(
            text = "$username, $age",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            )
        )
    }
}