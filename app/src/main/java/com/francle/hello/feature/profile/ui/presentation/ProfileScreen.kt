package com.francle.hello.feature.profile.ui.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
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
import com.francle.hello.core.ui.util.asString
import com.francle.hello.feature.profile.ui.event.ProfileEvent
import com.francle.hello.feature.profile.ui.presentation.components.BannerComponent
import com.francle.hello.feature.profile.ui.presentation.components.ProfileTopAppBar
import com.francle.hello.feature.profile.ui.presentation.components.UserInfoComponent
import com.francle.hello.feature.profile.ui.viewmodel.ProfileViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
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

    // Local Variables
    val context = LocalContext.current
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

    LaunchedEffect(lifecycle) {
        when (lifecycle) {
            Lifecycle.Event.ON_START -> {
                profileViewModel.getProfile(ownUserId)
            }
            else -> {}
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

    Column(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) {
        // Top App Bar
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
                    modifier = Modifier.fillMaxWidth().padding(SpaceMedium),
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
