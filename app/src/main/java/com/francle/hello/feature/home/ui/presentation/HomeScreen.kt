package com.francle.hello.feature.home.ui.presentation

import android.net.Uri
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import coil.compose.rememberAsyncImagePainter
import com.francle.hello.R
import com.francle.hello.core.ui.hub.navigation.destination.Destination
import com.francle.hello.core.ui.theme.SpaceSmall
import com.francle.hello.core.ui.util.asString
import com.francle.hello.feature.home.ui.presentation.components.postcard.ui.PostCard
import com.francle.hello.feature.home.ui.presentation.event.HomeEvent
import com.francle.hello.feature.home.ui.viewmodel.HomeViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.StyledPlayerView

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalPagerApi::class,
    ExperimentalComposeUiApi::class
)
@Composable
fun HomeScreen(
    modifier: Modifier,
    snackbarHostState: SnackbarHostState,
    onNavigate: (String) -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val posts = homeViewModel.posts.collectAsState().value
    val loading = homeViewModel.isLoading.collectAsState().value
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val lazyListState = rememberLazyListState()
    val mediaItems = homeViewModel.mediaItems.collectAsState().value
    val isMediaItemClicked = homeViewModel.isMediaItemClicked.collectAsState().value
    val currentIndex = homeViewModel.currentIndex.collectAsState().value
    val pagerState = rememberPagerState(currentIndex)
    var lifecycle by remember {
        mutableStateOf(Lifecycle.Event.ON_CREATE)
    }
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            lifecycle = event
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(currentIndex) {
        pagerState.scrollToPage(currentIndex)
    }

    LaunchedEffect(homeViewModel.responseChannel) {
        homeViewModel.responseChannel.collect { uiText ->
            snackbarHostState.showSnackbar(
                message = uiText.asString(context),
                duration = SnackbarDuration.Short
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) {
        CenterAlignedTopAppBar(
            title = {
                Text(text = stringResource(id = R.string.home))
            },
            scrollBehavior = scrollBehavior
        )

        if (loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(if (isSystemInDarkTheme()) Color.Gray else Color.White),
                contentAlignment = Alignment.Center
            ) {
                Column {
                    Text(text = "Loading", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(SpaceSmall))
                    CircularProgressIndicator()
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = lazyListState
        ) {
            items(posts) { post ->
                post?.let {
                    PostCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(SpaceSmall)
                            .animateContentSize()
                            .clickable { onNavigate(Destination.PostDetail.route + "/${it.id}") },
                        post = it
                    )
                }
            }
        }
    }

    if (isMediaItemClicked) {
        Dialog(
            onDismissRequest = {
                homeViewModel.onEvent(HomeEvent.DisMissFullScreen)
            },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnBackPress = true
            )
        ) {
            Box(
                modifier = modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                HorizontalPager(
                    count = mediaItems.size,
                    modifier = modifier.fillMaxSize(),
                    state = pagerState
                ) { page ->
                    mediaItems[page].let { pair ->
                        when (pair.fileName?.substringAfterLast(".")) {
                            "png", "jpg", "jpeg" -> {
                                Image(
                                    painter = rememberAsyncImagePainter(
                                        pair.postContentUrl
                                    ),
                                    contentDescription = stringResource(
                                        R.string.image_content
                                    ),
                                    modifier = Modifier
                                        .animateContentSize()
                                        .fillMaxSize()
                                )
                            }

                            "mp4" -> {
                                AndroidView(
                                    factory = { context ->
                                        StyledPlayerView(context).also { view ->
                                            view.player = homeViewModel.player.apply {
                                                prepare()
                                                setMediaItem(
                                                    MediaItem.fromUri(
                                                        Uri.parse(pair.postContentUrl)
                                                    )
                                                )
                                            }
                                            view.resizeMode =
                                                AspectRatioFrameLayout.RESIZE_MODE_FIT
                                        }
                                    },
                                    update = { view ->
                                        when (lifecycle) {
                                            Lifecycle.Event.ON_PAUSE -> {
                                                view.onPause()
                                                view.player?.pause()
                                            }

                                            Lifecycle.Event.ON_RESUME -> {
                                                view.onResume()
                                            }

                                            else -> Unit
                                        }
                                    },
                                    modifier = Modifier.animateContentSize().fillMaxSize()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
