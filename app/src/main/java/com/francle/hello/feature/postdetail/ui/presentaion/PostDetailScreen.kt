package com.francle.hello.feature.postdetail.ui.presentaion

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HideSource
import androidx.compose.material.icons.filled.PersonAddDisabled
import androidx.compose.material.icons.filled.PhotoAlbum
import androidx.compose.material.icons.filled.VerticalAlignTop
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dokar.sheets.BottomSheetLayout
import com.dokar.sheets.rememberBottomSheetState
import com.francle.hello.R
import com.francle.hello.core.ui.event.UiEvent
import com.francle.hello.core.ui.hub.navigation.destination.Destination
import com.francle.hello.core.ui.hub.navigation.util.toJson
import com.francle.hello.core.ui.hub.navigation.util.urlEncode
import com.francle.hello.core.ui.theme.SpaceMedium
import com.francle.hello.core.ui.theme.SpaceSmall
import com.francle.hello.core.ui.util.asString
import com.francle.hello.feature.home.ui.presentation.components.postcard.ui.components.BottomRow
import com.francle.hello.feature.home.ui.presentation.components.postcard.ui.components.HeadRow
import com.francle.hello.feature.home.ui.presentation.components.postcard.ui.components.PostMediaContent
import com.francle.hello.feature.postdetail.ui.event.DetailEvent
import com.francle.hello.feature.postdetail.ui.presentaion.components.DetailTopAppBar
import com.francle.hello.feature.postdetail.ui.viewmodel.PostDetailViewModel
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
    val post = postDetailViewModel.post.collectAsState().value
    val inputComment = postDetailViewModel.inputComment.collectAsState().value.text
    val isOwnPost = postDetailViewModel.isOwnPost.collectAsState().value

    // Local State
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberBottomSheetState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val scrollState = rememberScrollState()

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
                is UiEvent.Navigate -> {}
                UiEvent.NavigateUp -> {
                    onNavigateUp()
                }
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
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.wrapContentHeight()
            ) {
                TextField(
                    value = inputComment,
                    onValueChange = {
                        postDetailViewModel.onEvent(DetailEvent.InputComment(it))
                    },
                    modifier = Modifier.fillMaxSize(),
                    placeholder = {
                        Text(text = stringResource(R.string.comment_here))
                    },
                    trailingIcon = {
                        Icon(imageVector = Icons.Filled.PhotoAlbum, contentDescription = null)
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        focusedIndicatorColor = MaterialTheme.colorScheme.surface,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        }
    ) {
        Surface(
            modifier = Modifier
                .padding(it)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .verticalScroll(scrollState)
        ) {
            Column(
                modifier = Modifier.padding(SpaceSmall),
                verticalArrangement = Arrangement.spacedBy(SpaceSmall)
            ) {
                post?.apply {
                    HeadRow(
                        modifier = Modifier.fillMaxWidth(),
                        profileImageUrl = profileImageUrl,
                        username = username,
                        hashTag = hashTag,
                        onBottomSheetExpand = {
                            scope.launch {
                                postDetailViewModel.onEvent(DetailEvent.IsOwnPost(userId))
                                bottomSheetState.expand()
                            }
                        }
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
                            onNavigate(
                                Destination.FullScreenView.route +
                                    "/${post.toJson()?.urlEncode()}" + "/$index"
                            )
                        }
                    )

                    Column {
                        Divider()
                        BottomRow(
                            modifier = Modifier.fillMaxWidth(),
                            likeState = false,
                            onCommentClick = {},
                            onRepostClick = {},
                            onLikeClick = {},
                            onShareClick = {}
                        )
                        Divider()
                    }
                }
            }
        }
    }

    // Bottom Sheet
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
