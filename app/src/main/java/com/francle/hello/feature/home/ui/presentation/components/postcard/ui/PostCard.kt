package com.francle.hello.feature.home.ui.presentation.components.postcard.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.francle.hello.core.ui.theme.SpaceMedium
import com.francle.hello.core.ui.theme.SpaceSmall
import com.francle.hello.feature.home.domain.models.Post
import com.francle.hello.feature.home.ui.presentation.components.postcard.ui.components.BottomRow
import com.francle.hello.feature.home.ui.presentation.components.postcard.ui.components.ExpandableText
import com.francle.hello.feature.home.ui.presentation.components.postcard.ui.components.HeadRow
import com.francle.hello.feature.home.ui.presentation.components.postcard.ui.components.PostMediaContent
import com.francle.hello.feature.home.ui.presentation.components.postcard.ui.event.PostCardEvent
import com.francle.hello.feature.home.ui.presentation.components.postcard.viewmodel.unifiedPostCardViewModel

@Composable
fun PostCard(
    modifier: Modifier,
    post: Post,
    onBottomSheetExpand: () -> Unit,
    onMediaItemClick: (Int) -> Unit,
    onCommentClick: () -> Unit,
    onRepostClick: () -> Unit,
    onShareClick: () -> Unit,
    onProfileImageClick: () -> Unit
) {
    // ViewModel
    val postCardViewModel = unifiedPostCardViewModel(postId = post.id)
    val likeState = postCardViewModel.likeState.collectAsStateWithLifecycle().value

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
        postCardViewModel.onEvent(PostCardEvent.CheckLikeState)
    }

    Card(modifier = modifier) {
        HeadRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            profileImageUrl = post.profileImageUrl,
            username = post.username,
            hashTag = post.hashTag,
            onBottomSheetExpand = onBottomSheetExpand,
            onProfileImageClick = onProfileImageClick
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

        BottomRow(
            modifier = Modifier.fillMaxWidth(),
            likeState = likeState,
            onCommentClick = { onCommentClick() },
            onRepostClick = { onRepostClick() },
            onLikeClick = {
                postCardViewModel.onEvent(PostCardEvent.ClickLikeButton(postUserId = post.userId))
            },
            onShareClick = { onShareClick() }
        )
    }
}
