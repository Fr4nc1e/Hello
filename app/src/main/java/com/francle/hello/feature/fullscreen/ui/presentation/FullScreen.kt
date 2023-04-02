package com.francle.hello.feature.fullscreen.ui.presentation

import android.net.Uri
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import coil.compose.rememberAsyncImagePainter
import com.francle.hello.R
import com.francle.hello.core.ui.event.UiEvent
import com.francle.hello.core.ui.util.asString
import com.francle.hello.feature.fullscreen.ui.presentation.components.FullScreenBottomBar
import com.francle.hello.feature.fullscreen.ui.presentation.components.FullScreenTopAppBar
import com.francle.hello.feature.fullscreen.ui.presentation.event.FullScreenEvent
import com.francle.hello.feature.fullscreen.ui.viewmodel.FullScreenViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.StyledPlayerView

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FullScreen(
    modifier: Modifier,
    snackbarHostState: SnackbarHostState,
    onNavigate: (String) -> Unit,
    onNavigateUp: () -> Unit,
    fullScreenViewModel: FullScreenViewModel = hiltViewModel()
) {
    // ViewModel Variables
    val post = fullScreenViewModel.post.collectAsState().value
    val index = fullScreenViewModel.index.collectAsState().value
    val dropMenuVisibility = fullScreenViewModel.dropMenuVisible.collectAsState().value

    // Local Variables
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Local State
    val pagerState = rememberPagerState(index ?: 0)
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

    LaunchedEffect(fullScreenViewModel, context) {
        fullScreenViewModel.uiEventChannel.collect { uiEvent ->
            when (uiEvent) {
                is UiEvent.Navigate -> { onNavigate(uiEvent.route) }
                UiEvent.NavigateUp -> { onNavigateUp() }
                is UiEvent.Message -> {
                    snackbarHostState.showSnackbar(
                        message = uiEvent.message.asString(context),
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    LaunchedEffect(index) {
        index?.let { pagerState.scrollToPage(it) }
    }

    post?.postContentPairs?.let { pairs ->
        Scaffold(
            modifier = modifier,
            topBar = {
                FullScreenTopAppBar(
                    modifier = Modifier.fillMaxWidth(),
                    username = post.username,
                    hashTag = post.hashTag,
                    profileImageUrl = post.profileImageUrl,
                    onNavigationIconClick = {
                        fullScreenViewModel.onEvent(FullScreenEvent.NavigateUp)
                    },
                    actions = {
                        IconButton(onClick = { fullScreenViewModel.onEvent(FullScreenEvent.ShowDropMenu) }) {
                            Icon(
                                imageVector = Icons.Filled.MoreVert,
                                contentDescription = stringResource(R.string.drop_menu)
                            )
                        }
                        DropdownMenu(
                            expanded = dropMenuVisibility,
                            onDismissRequest = { fullScreenViewModel.onEvent(FullScreenEvent.ShowDropMenu) }
                        ) {
                            DropdownMenuItem(
                                text = { Text(text = stringResource(id = R.string.download)) },
                                leadingIcon = { Icon(
                                    imageVector = Icons.Filled.Download,
                                    contentDescription = stringResource(id = R.string.download)
                                ) },
                                onClick = {
                                    fullScreenViewModel.onEvent(
                                        FullScreenEvent.DownloadMedia(pairs[pagerState.currentPage])
                                    )
                                }
                            )
                        }
                    }
                )
            },
            bottomBar = {
                FullScreenBottomBar(
                    modifier = Modifier.fillMaxWidth(),
                    onCommentClick = {},
                    onRepostClick = {},
                    onLikeClick = {},
                    onShareClick = {}
                )
            }
        ) {
            HorizontalPager(
                count = pairs.size,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .background(Color.Black),
                state = pagerState
            ) { page ->
                pairs[page].let { pair ->
                    when (pair.fileName?.substringAfterLast(".")) {
                        "png", "jpg", "jpeg" -> {
                            Image(
                                painter = rememberAsyncImagePainter(pair.postContentUrl),
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
                                        view.player = fullScreenViewModel.player.apply {
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
                                modifier = Modifier
                                    .animateContentSize()
                                    .fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}