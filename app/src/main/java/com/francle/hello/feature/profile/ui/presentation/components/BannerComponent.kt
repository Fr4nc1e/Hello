package com.francle.hello.feature.profile.ui.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.francle.hello.R
import com.francle.hello.core.ui.theme.ProfilePictureSizeLarge
import com.francle.hello.core.ui.theme.SpaceLarge
import com.francle.hello.core.ui.theme.SpaceMedium
import com.francle.hello.core.ui.theme.SpaceSmall
import com.francle.hello.feature.profile.domain.model.User

@Composable
fun BannerComponent(
    modifier: Modifier,
    user: User?
) {
    Box(modifier = modifier.clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))) {
        Image(
            painter = rememberAsyncImagePainter(user?.bannerImageUrl),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .blur(
                    radiusX = 10.dp,
                    radiusY = 10.dp,
                    edgeTreatment = BlurredEdgeTreatment.Rectangle
                ),
            contentScale = ContentScale.Crop
        )

        Image(
            painter = rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current)
                    .data(user?.profileImageUrl)
                    .crossfade(true)
                    .build()
            ),
            contentDescription = stringResource(R.string.profile_image),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = SpaceLarge)
                .size(ProfilePictureSizeLarge)
                .clip(CircleShape)
        )

        user?.also { user ->
            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = SpaceLarge),
                verticalArrangement = Arrangement.spacedBy(SpaceSmall),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = user.username + ", " + (user.age ?: "18"),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(SpaceMedium),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(SpaceSmall),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Following: ")

                        Text(text = user.following.toString())
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(SpaceSmall),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Follower(s): ")

                        Text(text = user.followedBy.toString())
                    }
                }

                Text(text = user.bio ?: "Hello world.")
            }
        }
    }
}
