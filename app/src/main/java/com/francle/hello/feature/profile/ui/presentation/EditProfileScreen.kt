package com.francle.hello.feature.profile.ui.presentation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.SettingsBackupRestore
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.francle.hello.R
import com.francle.hello.core.ui.event.UiEvent
import com.francle.hello.core.ui.theme.SpaceMedium
import com.francle.hello.core.ui.theme.SpaceSmall
import com.francle.hello.core.ui.util.asString
import com.francle.hello.feature.profile.ui.event.EditEvent
import com.francle.hello.feature.profile.ui.presentation.components.EditBannerComponent
import com.francle.hello.feature.profile.ui.presentation.components.EditProfileTopAppBar
import com.francle.hello.feature.profile.ui.viewmodel.EditProfileViewModel
import com.mr0xf00.easycrop.ui.ImageCropperDialog
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    modifier: Modifier,
    snackbarHostState: SnackbarHostState,
    onNavigateUp: () -> Unit,
    editProfileViewModel: EditProfileViewModel = hiltViewModel()
) {
    // ViewModel
    val username = editProfileViewModel.username.collectAsStateWithLifecycle().value
    val age = editProfileViewModel.age.collectAsStateWithLifecycle().value
    val bio = editProfileViewModel.bio.collectAsStateWithLifecycle().value
    val profileImageUrl = editProfileViewModel.profileImageUrl.collectAsStateWithLifecycle().value
    val bannerImageUrl = editProfileViewModel.bannerImageUrl.collectAsStateWithLifecycle().value
    val chosenProfileImageUri = editProfileViewModel.chosenProfileImageUri
        .collectAsStateWithLifecycle().value
    val chosenBannerImageUri = editProfileViewModel.chosenBannerImageUri
        .collectAsStateWithLifecycle().value
    val loading = editProfileViewModel.loading.collectAsStateWithLifecycle().value

    // Local
    val context = LocalContext.current

    // Launch Effect
    LaunchedEffect(editProfileViewModel, context) {
        editProfileViewModel.resultChannel.collectLatest { uiEvent ->
            when (uiEvent) {
                is UiEvent.Message -> {
                    snackbarHostState.showSnackbar(
                        message = uiEvent.message.asString(context),
                        duration = SnackbarDuration.Short
                    )
                }
                UiEvent.NavigateUp -> {
                    onNavigateUp()
                }
                else -> Unit
            }
        }
    }

    // Profile Image Launcher
    val profileImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.also {
                editProfileViewModel.onEvent(
                    EditEvent.CropImage(
                        uri = it,
                        type = 0,
                        context = context
                    )
                )
            }
        }
    )

    // Banner Image Launcher
    val bannerImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.also {
                editProfileViewModel.onEvent(
                    EditEvent.CropImage(
                        uri = it,
                        type = 1,
                        context = context
                    )
                )
            }
        }
    )

    val cropperState = editProfileViewModel.imageCropper.cropState
    if (cropperState != null) {
        ImageCropperDialog(
            state = cropperState,
            topBar = {
                TopAppBar(
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = { it.done(accept = false) }) {
                            Icon(Icons.Default.ArrowBack, null)
                        }
                    },
                    actions = {
                        IconButton(onClick = { it.reset() }) {
                            Icon(Icons.Filled.SettingsBackupRestore, null)
                        }
                        IconButton(onClick = { it.done(accept = true) }, enabled = !it.accepted) {
                            Icon(Icons.Default.Done, null)
                        }
                    },
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            EditProfileTopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(R.string.edit_your_profile),
                loading = loading,
                onNavigateBackClick = { onNavigateUp() },
                onCompletedClick = { editProfileViewModel.onEvent(EditEvent.EditCompleted) }
            )
        }
    ) {
        Column(modifier = Modifier.padding(it)) {
            EditBannerComponent(
                modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f),
                profileImageUrl = profileImageUrl,
                bannerImageUrl = bannerImageUrl,
                chosenProfileImageUri = chosenProfileImageUri,
                chosenBannerImageUri = chosenBannerImageUri,
                onProfileImageClick = {
                    profileImageLauncher.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                },
                onBannerImageClick = {
                    bannerImageLauncher.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                }
            )

            Column(
                modifier = Modifier.fillMaxWidth().padding(SpaceSmall),
                verticalArrangement = Arrangement.spacedBy(SpaceMedium)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(SpaceSmall)) {
                    Text(text = "user name")
                    TextField(
                        value = username,
                        onValueChange = { text ->
                            editProfileViewModel.onEvent(EditEvent.EditUserName(text))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !loading,
                        shape = RoundedCornerShape(16.dp),
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }
                Column(modifier = Modifier.fillMaxWidth().padding(SpaceSmall)) {
                    Text(text = "age")
                    TextField(
                        value = age.toString(),
                        onValueChange = { text ->
                            editProfileViewModel.onEvent(EditEvent.EditAge(text))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !loading,
                        shape = RoundedCornerShape(16.dp),
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }
                Column(modifier = Modifier.fillMaxWidth().padding(SpaceSmall)) {
                    Text(text = "self introduction")
                    TextField(
                        value = bio ?: "Hello World.",
                        onValueChange = { text ->
                            editProfileViewModel.onEvent(EditEvent.EditBio(text))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !loading,
                        shape = RoundedCornerShape(16.dp),
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }
            }
        }
    }
}
