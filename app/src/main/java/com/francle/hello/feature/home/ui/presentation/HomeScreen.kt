package com.francle.hello.feature.home.ui.presentation

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HideSource
import androidx.compose.material.icons.filled.PersonAddDisabled
import androidx.compose.material.icons.filled.VerticalAlignTop
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import com.francle.hello.core.ui.hub.navigation.destination.Destination
import com.francle.hello.core.ui.hub.navigation.util.toJson
import com.francle.hello.core.ui.hub.navigation.util.urlEncode
import com.francle.hello.core.ui.theme.SpaceMedium
import com.francle.hello.core.ui.theme.SpaceSmall
import com.francle.hello.core.ui.util.asString
import com.francle.hello.feature.home.domain.model.Post
import com.francle.hello.feature.home.ui.presentation.components.postcard.ui.PostCard
import com.francle.hello.feature.home.ui.presentation.event.HomeEvent
import com.francle.hello.feature.home.ui.viewmodel.HomeViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier,
    snackbarHostState: SnackbarHostState,
    onNavigate: (String) -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    // ViewModel Variables
    val posts = homeViewModel.posts.collectAsState().value
    val loading = homeViewModel.isLoading.collectAsState().value
    val isRefreshing = homeViewModel.isRefreshing.collectAsState().value
    val isEndReach = homeViewModel.isEndReach.collectAsState().value
    val clickedMoreVert = homeViewModel.clickedMoreVert.collectAsState().value
    val isOwnPost = homeViewModel.isOwnPost.collectAsState().value

    // Local Variable
    val context = LocalContext.current

    // Local State
    val refreshState = rememberSwipeRefreshState(isRefreshing)
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberBottomSheetState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    // LaunchEffect
    LaunchedEffect(homeViewModel.responseChannel, context) {
        homeViewModel.responseChannel.collect { uiText ->
            snackbarHostState.showSnackbar(
                message = uiText.asString(context),
                duration = SnackbarDuration.Short
            )
        }
    }

    // UI
    SwipeRefresh(
        state = refreshState,
        onRefresh = { homeViewModel.onEvent(HomeEvent.Refresh) },
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            // Top App Bar
            CenterAlignedTopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.home))
                },
                scrollBehavior = scrollBehavior
            )

            // Loading Progress
            if (loading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column {
                        Text(text = "Loading", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(SpaceSmall))
                        CircularProgressIndicator()
                    }
                }
            }

            // Posts
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = lazyListState,
                contentPadding = PaddingValues(SpaceSmall),
                verticalArrangement = Arrangement.spacedBy(SpaceSmall)
            ) {
                itemsIndexed(
                    items = posts,
                    key = { _: Int, item: Post? ->
                        item!!.id
                    },
                    contentType = { _: Int, item: Post? ->
                        item
                    }
                ) { index: Int, item: Post? ->
                    if (index >= posts.size - 1 && !isEndReach && !loading && posts.size >= 20) {
                        homeViewModel.onEvent(HomeEvent.LoadNextItems)
                    }
                    item?.let { post ->
                        PostCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateContentSize()
                                .clickable {
                                    onNavigate(Destination.PostDetail.route + "/${post.id}")
                                },
                            post = post,
                            onBottomSheetExpand = {
                                homeViewModel.onEvent(HomeEvent.IsOwnPost(post.userId))
                                homeViewModel.onEvent(HomeEvent.ClickMoreVert(post))
                                scope.launch {
                                    bottomSheetState.expand()
                                }
                            },
                            onMediaItemClick = { index ->
                                onNavigate(
                                    Destination.FullScreenView.route +
                                            "/${post.toJson()?.urlEncode()}" + "/$index"
                                )
                            },
                            onCommentClick = {},
                            onRepostClick = {},
                            onLikeClick = {},
                            onShareClick = {}
                        )
                    }
                }
                item { 
                    Spacer(modifier = Modifier.height(70.dp))
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
                                        homeViewModel.onEvent(HomeEvent.DeletePost)
                                        scope.launch {
                                            bottomSheetState.collapse()
                                        }
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
                                    text = "Unfollow ${clickedMoreVert?.hashTag}",
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
                                    text = "Hide ${clickedMoreVert?.hashTag}",
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
                                    text = "Block ${clickedMoreVert?.hashTag}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
