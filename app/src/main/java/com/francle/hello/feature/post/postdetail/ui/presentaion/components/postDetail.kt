package com.francle.hello.feature.post.postdetail.ui.presentaion.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import com.francle.hello.core.ui.theme.SpaceSmall
import com.francle.hello.feature.home.domain.models.Post
import com.francle.hello.feature.home.ui.presentation.components.postcard.ui.components.BottomRow
import com.francle.hello.feature.home.ui.presentation.components.postcard.ui.components.HeadRow
import com.francle.hello.feature.home.ui.presentation.components.postcard.ui.components.PostMediaContent

fun LazyListScope.postDetail(
    modifier: Modifier,
    post: Post?,
    likeState: Boolean,
    onBottomSheetExpand: () -> Unit,
    onProfileImageClick: () -> Unit,
    onMediaItemClick: (Int) -> Unit,
    onCommentClick: () -> Unit,
    onLikeClick: () -> Unit
) {
    item {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(SpaceSmall)
        ) {
            post?.apply {
                HeadRow(
                    modifier = Modifier.fillMaxWidth(),
                    profileImageUrl = profileImageUrl,
                    username = username,
                    hashTag = hashTag,
                    onBottomSheetExpand = { onBottomSheetExpand() },
                    onProfileImageClick = { onProfileImageClick() }
                )

                postText?.let { text ->
                    Text(
                        text = text,
                        modifier = Modifier.padding(horizontal = SpaceSmall)
                    )
                }

                PostMediaContent(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize(),
                    postContentPairs = postContentPairs,
                    onMediaItemClick = { index ->
                        onMediaItemClick(index)
                    }
                )

                Column {
                    Divider()
                    BottomRow(
                        modifier = Modifier.fillMaxWidth(),
                        likeState = likeState,
                        onCommentClick = { onCommentClick() },
                        onRepostClick = {},
                        onLikeClick = { onLikeClick() },
                        onShareClick = {}
                    )
                    Divider()
                }
            }
        }
    }
}
