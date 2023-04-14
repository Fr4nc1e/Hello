package com.francle.hello.feature.profile.ui.presentation.components

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.francle.hello.R
import com.francle.hello.core.ui.theme.IconSizeLarge
import com.francle.hello.core.ui.theme.ProfilePictureSizeLarge
import com.francle.hello.core.ui.theme.SpaceLarge

@Composable
fun EditBannerComponent(
    modifier: Modifier,
    profileImageUrl: String?,
    bannerImageUrl: String?,
    chosenProfileImageUri: Uri?,
    chosenBannerImageUri: Uri?,
    onProfileImageClick: () -> Unit,
    onBannerImageClick: () -> Unit
) {
    Box(modifier = modifier.clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))) {
        Image(
            painter = rememberAsyncImagePainter(chosenBannerImageUri ?: bannerImageUrl),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .clickable { 
                    onBannerImageClick()
                },
            contentScale = ContentScale.Crop,
            colorFilter = ColorFilter.tint(Color.Gray, blendMode = BlendMode.Darken)
        )

        Icon(
            imageVector = Icons.Filled.AddPhotoAlternate,
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Center)
                .size(IconSizeLarge),
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )

        Box(
            modifier = Modifier
                .wrapContentSize()
                .clip(CircleShape)
                .align(Alignment.CenterStart)
                .padding(start = SpaceLarge)
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current)
                        .data(chosenProfileImageUri ?: profileImageUrl)
                        .crossfade(true)
                        .build()
                ),
                contentDescription = stringResource(R.string.profile_image),
                contentScale = ContentScale.Crop,
                colorFilter = ColorFilter.tint(Color.Gray, blendMode = BlendMode.Darken),
                modifier = Modifier
                    .size(ProfilePictureSizeLarge)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        shape = CircleShape
                    )
                    .clip(CircleShape)
                    .clickable {
                        onProfileImageClick()
                    }
            )

            Icon(
                imageVector = Icons.Filled.AddPhotoAlternate,
                contentDescription = null,
                modifier = Modifier
                    .size(IconSizeLarge)
                    .align(Alignment.Center),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}
