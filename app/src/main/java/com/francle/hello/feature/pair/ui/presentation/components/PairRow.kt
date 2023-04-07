package com.francle.hello.feature.pair.ui.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.rememberAsyncImagePainter
import com.francle.hello.R
import com.francle.hello.core.ui.theme.ProfilePictureSizeMedium
import com.francle.hello.core.ui.theme.SpaceSmall


@Composable
fun PairRow(
    modifier: Modifier,
    profileImageUrl: String,
    onPairClick: () -> Unit
) {
    Box(
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .padding(SpaceSmall)
                .size(ProfilePictureSizeMedium)
                .clip(CircleShape)
                .align(Alignment.CenterStart),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = profileImageUrl),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(text = "Welcome!!!")
            Text(text = "Let's find a match!")
        }

        Box(
            modifier = Modifier
                .padding(SpaceSmall)
                .size(ProfilePictureSizeMedium)
                .clip(CircleShape)
                .align(Alignment.CenterEnd)
                .clickable {
                    onPairClick()
                },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
