package com.francle.hello.feature.profile.ui.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.francle.hello.R
import com.francle.hello.core.ui.event.UiEvent
import com.francle.hello.core.ui.theme.SpaceMedium
import com.francle.hello.core.ui.theme.SpaceSmall
import com.francle.hello.core.ui.util.asString
import com.francle.hello.feature.profile.ui.event.ProfileEvent
import com.francle.hello.feature.profile.ui.presentation.components.BannerComponent
import com.francle.hello.feature.profile.ui.presentation.components.ProfileTopAppBar
import com.francle.hello.feature.profile.ui.presentation.components.UserInfoComponent
import com.francle.hello.feature.profile.ui.viewmodel.ProfileViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun ProfileScreen(
    modifier: Modifier,
    snackbarHostState: SnackbarHostState,
    onLogOut: () -> Unit,
    onNavigate: (String) -> Unit,
    onNavigateUp: () -> Unit,
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    // ViewModel State
    val ownUserId = profileViewModel.ownUserId.collectAsStateWithLifecycle().value
    val user = profileViewModel.user.collectAsStateWithLifecycle().value
    val showDropMenu = profileViewModel.showDropMenu.collectAsStateWithLifecycle().value
    val showAlertDialog = profileViewModel.showLogOutDialog.collectAsStateWithLifecycle().value
    val isOwnProfile = profileViewModel.isOwnProfile.collectAsStateWithLifecycle().value
    val tabIndex = profileViewModel.tabIndex.collectAsStateWithLifecycle().value
    
    // Local Variables
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val lifecycleOwner = LocalLifecycleOwner.current
    var lifecycle by remember { mutableStateOf(Lifecycle.Event.ON_CREATE) }
    val pagerState = rememberPagerState(
        initialPage = ProfileTabContent.Posts.ordinal
    )
    val scope = rememberCoroutineScope()

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

    LaunchedEffect((lifecycle == Lifecycle.Event.ON_START)) {
        if (isOwnProfile) {
            profileViewModel.getProfile(ownUserId)
        }
    }

    // Launch Effect
    LaunchedEffect(profileViewModel, context) {
        profileViewModel.resultChannel.collectLatest { uiEvent ->
            when (uiEvent) {
                is UiEvent.Message -> {
                    snackbarHostState.showSnackbar(
                        message = uiEvent.message.asString(context),
                        duration = SnackbarDuration.Short
                    )
                }
                is UiEvent.Navigate -> {
                    onNavigate(uiEvent.route)
                }
                UiEvent.LogOut -> {
                    onLogOut()
                }
                else -> Unit
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            ProfileTopAppBar(
                modifier = Modifier.fillMaxWidth(),
                showDropMenu = showDropMenu,
                isOwnProfile = isOwnProfile,
                scrollBehavior = scrollBehavior,
                onNavigationIconClick = { onNavigateUp() },
                onClickMoreVert = { profileViewModel.onEvent(ProfileEvent.ClickMoreVert) },
                onClickLogOut = { profileViewModel.onEvent(ProfileEvent.ClickLogOut) },
                onEditClick = { profileViewModel.onEvent(ProfileEvent.ClickEdit) },
                onMessageClick = {
                    profileViewModel.onEvent(ProfileEvent.ClickMessage)
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    BannerComponent(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                            .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)),
                        user = user
                    )

                    UserInfoComponent(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(SpaceMedium),
                        username = user?.username,
                        age = user?.age.toString(),
                        bio = user?.bio,
                        postCount = user?.postCount ?: 0,
                        likeCount = user?.likedCount ?: 0,
                        followingCount = user?.following ?: 0,
                        followerCount = user?.followedBy ?: 0,
                        onFollowingClick = {},
                        onFollowerClick = {}
                    )
                }
            }

            Column {
                TabRow(
                    selectedTabIndex = tabIndex,
                    modifier = Modifier
                        .padding(SpaceSmall)
                        .clip(RoundedCornerShape(50))
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(50)
                        ),
                    indicator = {},
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    ProfileTabContent.values().forEachIndexed { index, profileTabContent ->
                        Tab(
                            selected = tabIndex == index,
                            onClick = {
                                scope.launch {
                                    profileViewModel.onEvent(ProfileEvent.SwitchTab(index))
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            modifier = Modifier.clip(RoundedCornerShape(50)).background(
                                when (tabIndex == index) {
                                    true -> {
                                        MaterialTheme.colorScheme.onPrimary
                                    }
                                    false -> {
                                        MaterialTheme.colorScheme.primary
                                    }
                                }
                            ),
                            text = {
                                Text(text = profileTabContent.tabName)
                            },
                            icon = {
                                Icon(
                                    imageVector = profileTabContent.imageVector,
                                    contentDescription = null
                                )
                            },
                            selectedContentColor = MaterialTheme.colorScheme.primary,
                            unselectedContentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }

                HorizontalPager(
                    count = ProfileTabContent.values().size,
                    modifier = Modifier.fillMaxWidth(),
                    state = pagerState
                ) { page ->
                    when (page) {
                        ProfileTabContent.Posts.ordinal -> {
                            LazyColumn {
                                items(100) {
                                    Text(text = "posts")
                                }
                            }
                        }
                        ProfileTabContent.Comments.ordinal -> {
                            LazyColumn {
                                items(200) {
                                    Text(text = "comments")
                                }
                            }
                        }
                        ProfileTabContent.Likes.ordinal -> {
                            LazyColumn {
                                items(200) {
                                    Text(text = "likes")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    if (showAlertDialog) {
        AlertDialog(
            onDismissRequest = { profileViewModel.onEvent(ProfileEvent.ClickLogOut) },
            confirmButton = {
                TextButton(
                    onClick = {
                        profileViewModel.onEvent(ProfileEvent.ClickLogOut)
                        profileViewModel.onEvent(ProfileEvent.ClickMoreVert)
                        profileViewModel.onEvent(ProfileEvent.LogOut)
                    }
                ) {
                    Text(text = stringResource(R.string.yes))
                }
            },
            dismissButton = {
                TextButton(onClick = { profileViewModel.onEvent(ProfileEvent.ClickLogOut) }) {
                    Text(text = stringResource(R.string.no))
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Filled.ExitToApp,
                    contentDescription = null
                )
            },
            title = {
                Text(text = stringResource(R.string.log_out))
            },
            text = {
                Text(text = stringResource(R.string.you_will_go_to_login_screen))
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}
