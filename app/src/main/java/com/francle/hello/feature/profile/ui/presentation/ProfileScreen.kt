package com.francle.hello.feature.profile.ui.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.francle.hello.R
import com.francle.hello.core.ui.event.UiEvent
import com.francle.hello.core.ui.util.asString
import com.francle.hello.feature.profile.ui.event.ProfileEvent
import com.francle.hello.feature.profile.ui.presentation.components.BannerComponent
import com.francle.hello.feature.profile.ui.presentation.components.ProfileTopAppBar
import com.francle.hello.feature.profile.ui.viewmodel.ProfileViewModel

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
    val user = profileViewModel.user.collectAsStateWithLifecycle().value
    val showDropMenu = profileViewModel.showDropMenu.collectAsStateWithLifecycle().value
    val showAlertDialog = profileViewModel.showAlertDialog.collectAsStateWithLifecycle().value
    val isOwnProfile = profileViewModel.isOwnProfile.collectAsStateWithLifecycle().value

    // Local Variables
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    // Launch Effect
    LaunchedEffect(profileViewModel, context) {
        profileViewModel.resultChannel.collect { uiEvent ->
            when (uiEvent) {
                is UiEvent.Message -> {
                    snackbarHostState.showSnackbar(
                        message = uiEvent.message.asString(context),
                        duration = SnackbarDuration.Short
                    )
                }
                is UiEvent.Navigate -> {}
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
            onEditClick = {}
        )

        BannerComponent(
            modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f),
            user = user
        )
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
            icon = { Icon(
                imageVector = Icons.Filled.ExitToApp,
                contentDescription = null
            ) },
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
