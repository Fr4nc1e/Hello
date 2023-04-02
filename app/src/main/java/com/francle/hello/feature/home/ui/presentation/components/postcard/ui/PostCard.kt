package com.francle.hello.feature.home.ui.presentation.components.postcard.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import com.francle.hello.core.ui.theme.SpaceMedium
import com.francle.hello.feature.home.domain.model.Post
import com.francle.hello.feature.home.ui.presentation.components.postcard.ui.components.BottomRow
import com.francle.hello.feature.home.ui.presentation.components.postcard.ui.components.ExpandableText
import com.francle.hello.feature.home.ui.presentation.components.postcard.ui.components.HeadRow
import com.francle.hello.feature.home.ui.presentation.components.postcard.ui.components.PostMediaContent

@Composable
fun PostCard(
    modifier: Modifier,
    post: Post,
    onBottomSheetExpand: () -> Unit,
    onMediaItemClick: (Int) -> Unit
) {
    Card(modifier = modifier) {
        HeadRow(
            modifier = Modifier.fillMaxWidth(),
            profileImageUrl = post.profileImageUrl,
            username = post.username,
            hashTag = post.hashTag,
            onBottomSheetExpand = onBottomSheetExpand
        )

        post.postText?.let {
            ExpandableText(
                text = it,
                modifier = Modifier.padding(start = SpaceMedium)
            )
        }

        PostMediaContent(
            modifier = Modifier
                .fillMaxWidth()
                .align(CenterHorizontally),
            postContentPairs = post.postContentPairs,
            onMediaItemClick = onMediaItemClick
        )
        
        BottomRow(modifier = Modifier.fillMaxWidth())
    }
}
