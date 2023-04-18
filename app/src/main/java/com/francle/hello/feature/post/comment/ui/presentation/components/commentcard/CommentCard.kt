package com.francle.hello.feature.post.comment.ui.presentation.components.commentcard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.francle.hello.core.ui.theme.SpaceSmall
import com.francle.hello.feature.home.ui.presentation.components.postcard.ui.components.BottomRow
import com.francle.hello.feature.home.ui.presentation.components.postcard.ui.components.HeadRow
import com.francle.hello.feature.post.comment.domain.models.Comment
import com.francle.hello.feature.post.comment.ui.event.CommentCardEvent
import com.francle.hello.feature.post.comment.ui.presentation.components.commentcard.components.CommentMediaContent
import com.francle.hello.feature.post.comment.ui.presentation.components.commentcard.viewmodel.unifiedCommentCardViewModel

@Composable
fun CommentCard(
    modifier: Modifier,
    comment: Comment,
    onBottomSheetExpand: () -> Unit,
    onMediaItemClick: (Int) -> Unit,
    onCommentClick: () -> Unit,
    onRepostClick: () -> Unit,
    onShareClick: () -> Unit,
    onProfileImageClick: () -> Unit
) {
    val commentCardViewModel = unifiedCommentCardViewModel(commentId = comment.commentId)
    val likeState = commentCardViewModel.likeState.collectAsStateWithLifecycle().value
    val lifecycleOwner = LocalLifecycleOwner.current
    var lifecycle by remember { mutableStateOf(Lifecycle.Event.ON_CREATE) }

    // Launch Effect
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            lifecycle = event
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(lifecycle) {
        commentCardViewModel.onEvent(CommentCardEvent.CheckLikeState)
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(SpaceSmall)
    ) {
        HeadRow(
            modifier = Modifier.fillMaxWidth(),
            profileImageUrl = comment.profileImageUrl,
            username = comment.arrowBackUsername,
            hashTag = comment.hashTag,
            onBottomSheetExpand = {
                onBottomSheetExpand()
            },
            onProfileImageClick = {
                onProfileImageClick()
            }
        )

        comment.commentText?.let { text ->
            Text(
                text = text,
                modifier = Modifier.padding(horizontal = SpaceSmall)
            )
        }
        
        CommentMediaContent(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally),
            mediaUrls = comment.commentMediaUrls,
            onMediaItemClick = onMediaItemClick
        )

        BottomRow(
            modifier = Modifier.fillMaxWidth(),
            likeState = likeState,
            onCommentClick = { onCommentClick() },
            onRepostClick = { onRepostClick() },
            onLikeClick = {
                commentCardViewModel.onEvent(
                    CommentCardEvent.ClickLikeButton(commentUserId = comment.arrowBackUserId)
                )
            },
            onShareClick = { onShareClick() }
        )
        
        Divider()
    }
}
