package com.francle.hello.feature.post.postdetail.ui.presentaion

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HideSource
import androidx.compose.material.icons.filled.PersonAddDisabled
import androidx.compose.material.icons.filled.VerticalAlignTop
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dokar.sheets.BottomSheetLayout
import com.dokar.sheets.rememberBottomSheetState
import com.francle.hello.R
import com.francle.hello.core.ui.event.UiEvent
import com.francle.hello.core.ui.hub.presentation.navigation.destination.Destination
import com.francle.hello.core.ui.hub.presentation.navigation.util.toJson
import com.francle.hello.core.ui.hub.presentation.navigation.util.urlEncode
import com.francle.hello.core.ui.theme.SpaceMedium
import com.francle.hello.core.ui.theme.SpaceSmall
import com.francle.hello.core.ui.util.asString
import com.francle.hello.feature.post.comment.domain.models.CommentType
import com.francle.hello.feature.post.like.util.ForwardEntityType
import com.francle.hello.feature.post.postdetail.ui.event.DetailEvent
import com.francle.hello.feature.post.postdetail.ui.presentaion.components.DetailTopAppBar
import com.francle.hello.feature.post.postdetail.ui.presentaion.components.commentArea
import com.francle.hello.feature.post.postdetail.ui.presentaion.components.postDetail
import com.francle.hello.feature.post.postdetail.ui.viewmodel.PostDetailViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    modifier: Modifier,
    snackbarHostState: SnackbarHostState,
    onNavigate: (String) -> Unit,
    onNavigateUp: () -> Unit,
    postDetailViewModel: PostDetailViewModel = hiltViewModel()
) {
    // ViewModel State
    val post = postDetailViewModel.post.collectAsStateWithLifecycle().value
    val isOwnPost = postDetailViewModel.isOwnPost.collectAsStateWithLifecycle().value
    val likeState = postDetailViewModel.likeState.collectAsStateWithLifecycle().value
    val comments = postDetailViewModel.comments.collectAsStateWithLifecycle().value
    val loading = postDetailViewModel.likeState.collectAsStateWithLifecycle().value
    val isEndReach = postDetailViewModel.isEndReach.collectAsStateWithLifecycle().value

    // Local State
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberBottomSheetState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
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

    LaunchedEffect(lifecycle == Lifecycle.Event.ON_START) {
        postDetailViewModel.onEvent(DetailEvent.CheckLikeState)
    }

    // LaunchEffect
    LaunchedEffect(postDetailViewModel.responseChannel, context) {
        postDetailViewModel.responseChannel.collect { uiEvent ->
            when (uiEvent) {
                is UiEvent.Message -> {
                    snackbarHostState.showSnackbar(
                        message = uiEvent.message.asString(context),
                        duration = SnackbarDuration.Short
                    )
                }
                is UiEvent.Navigate -> { onNavigate(uiEvent.route) }
                UiEvent.NavigateUp -> {
                    onNavigateUp()
                }

                else -> {}
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxWidth(),
        topBar = {
            DetailTopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(R.string.post_detail),
                onNavigateUp = onNavigateUp,
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    postDetailViewModel.onEvent(
                        DetailEvent.Navigate(
                            Destination.CreateComment.route +
                                "/${post?.id}" +
                                "/${post?.userId}" +
                                "/${CommentType.POST.ordinal}"
                        )
                    )
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.AddCircle,
                    contentDescription = null
                )
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(it)
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = PaddingValues(SpaceSmall),
            verticalArrangement = Arrangement.spacedBy(SpaceSmall)
        ) {
            postDetail(
                modifier = Modifier.fillMaxWidth(),
                post = post,
                likeState = likeState,
                onBottomSheetExpand = {
                    scope.launch {
                        postDetailViewModel.onEvent(
                            DetailEvent.IsOwnPost(post?.userId ?: "")
                        )
                        bottomSheetState.expand()
                    }
                },
                onProfileImageClick = {
                    postDetailViewModel.onEvent(
                        DetailEvent.Navigate(
                            Destination.Profile.route + "/${post?.userId}"
                        )
                    )
                },
                onMediaItemClick = { index ->
                    postDetailViewModel.onEvent(
                        DetailEvent.Navigate(
                            Destination.FullScreenView.route +
                                "/${post.toJson()?.urlEncode()}" + "/$index"
                        )
                    )
                },
                onCommentClick = {
                    postDetailViewModel.onEvent(
                        DetailEvent.Navigate(
                            Destination.CreateComment.route + "/${post?.id}" +
                                "/${post?.userId}" +
                                "/${CommentType.POST.ordinal}"
                        )
                    )
                },
                onLikeClick = {
                    postDetailViewModel.onEvent(
                        DetailEvent.ClickLikeButton(
                            ForwardEntityType.POST
                        )
                    )
                }
            )
            
            commentArea(
                comments = comments,
                onLoadItems = { index ->
                    if (
                        index >= comments.size - 1 &&
                        !isEndReach &&
                        !loading &&
                        comments.size >= 20
                    ) { postDetailViewModel.onEvent(DetailEvent.LoadItems) }
                },
                onBottomSheetExpand = {},
                onMediaItemClick = {},
                onCommentClick = { comment ->
                    postDetailViewModel.onEvent(
                        DetailEvent.Navigate(
                            Destination.CreateComment.route +
                                "/${comment.commentId}" +
                                "/${comment.arrowBackUserId}" +
                                "/${CommentType.COMMENT.ordinal}"
                        )
                    )
                },
                onRepostClick = {},
                onShareClick = {},
                onProfileImageClick = { route ->
                    postDetailViewModel.onEvent(DetailEvent.Navigate(route))
                }
            )
            
            item { Spacer(modifier = Modifier.height(70.dp)) }
        }
    }

    // bottom Sheet
    if (bottomSheetState.visible) {
        BottomSheetLayout(
            state = bottomSheetState,
            modifier = Modifier
                .fillMaxWidth()
                .clip(
                    RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                )
        ) {
            when (isOwnPost) {
                true -> {
                    Column(
                        modifier = Modifier.padding(SpaceMedium),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(SpaceMedium)
                                .clickable {
                                    postDetailViewModel.onEvent(DetailEvent.DeletePost)
                                },
                            horizontalArrangement = Arrangement.spacedBy(SpaceMedium),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = null
                            )
                            Text(
                                text = stringResource(R.string.delete_this_post),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(SpaceMedium),
                            horizontalArrangement = Arrangement.spacedBy(SpaceMedium),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.VerticalAlignTop,
                                contentDescription = null
                            )
                            Text(
                                text = stringResource(R.string.put_this_post_on_the_top_of_profile),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
                false -> {
                    Column(
                        modifier = Modifier.padding(SpaceMedium),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(SpaceMedium),
                            horizontalArrangement = Arrangement.spacedBy(SpaceMedium),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PersonAddDisabled,
                                contentDescription = null
                            )
                            Text(
                                text = "Unfollow ${post?.hashTag}",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(SpaceMedium),
                            horizontalArrangement = Arrangement.spacedBy(SpaceMedium)
                        ) {
                            Icon(imageVector = Icons.Filled.HideSource, contentDescription = null)
                            Text(
                                text = "Hide ${post?.hashTag}",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(SpaceMedium),
                            horizontalArrangement = Arrangement.spacedBy(SpaceMedium)
                        ) {
                            Icon(imageVector = Icons.Filled.Block, contentDescription = null)
                            Text(
                                text = "Block ${post?.hashTag}",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
        }
    }
}
