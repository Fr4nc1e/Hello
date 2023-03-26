package com.francle.hello.feature.home.ui.presentation.components.postcard.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import com.francle.hello.core.ui.theme.SpaceMedium
import com.francle.hello.core.ui.theme.SpaceSmall
import com.francle.hello.feature.home.domain.model.Post
import com.francle.hello.feature.home.ui.presentation.components.postcard.ui.components.HeadRow
import com.francle.hello.feature.home.ui.presentation.components.postcard.ui.components.PostMediaContent

@Composable
fun PostCard(
    modifier: Modifier,
    post: Post
) {
    Card(modifier = modifier) {
        HeadRow(
            modifier = Modifier.fillMaxWidth(),
            profileImageUrl = post.profileImageUrl,
            username = post.username
        )

        post.postText?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = SpaceMedium)
            )
        }

        PostMediaContent(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall)
                .align(CenterHorizontally),
            postContentPairs = post.postContentPairs
        )
    }
}
