package com.francle.hello.feature.post.createpost.ui.presentation

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.SettingsBackupRestore
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.francle.hello.R
import com.francle.hello.core.data.file.toUri
import com.francle.hello.core.ui.event.UiEvent
import com.francle.hello.core.ui.theme.ProfilePictureSizeSmall
import com.francle.hello.core.ui.theme.SpaceSmall
import com.francle.hello.core.ui.util.asString
import com.francle.hello.feature.post.createpost.ui.presentation.components.CreatePostBottomBar
import com.francle.hello.feature.post.createpost.ui.presentation.components.CreatePostTopAppBar
import com.francle.hello.feature.post.createpost.ui.presentation.event.CreatePostEvent
import com.francle.hello.feature.post.createpost.ui.viewmodel.CreatePostViewModel
import com.mr0xf00.easycrop.ui.ImageCropperDialog
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import com.skydoves.balloon.compose.Balloon
import com.skydoves.balloon.compose.rememberBalloonBuilder
import com.skydoves.balloon.compose.setBackgroundColor
import com.skydoves.balloon.compose.setTextColor
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    modifier: Modifier,
    snackbarHostState: SnackbarHostState,
    onNavigateUp: () -> Unit,
    createPostViewModel: CreatePostViewModel = hiltViewModel()
) {
    // ViewModel Variables
    val profileImageUrl = createPostViewModel.profileImageUrl.collectAsState().value
    val chosenMediaUriList = createPostViewModel.chosenContentUriList.collectAsState().value
    val postText = createPostViewModel.postText.collectAsState().value
    val loading = createPostViewModel.isLoading.collectAsState().value

    // Local Variables
    val context = LocalContext.current
    val balloonBuilder = rememberBalloonBuilder {
        setArrowSize(10)
        setArrowPosition(0.5f)
        setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
        setWidth(BalloonSizeSpec.WRAP)
        setHeight(BalloonSizeSpec.WRAP)
        setPadding(12)
        setMarginHorizontal(12)
        setCornerRadius(8f)
        setTextColor(Color.Black) // set text color with compose color.
        setBackgroundColor(Color.White)
        setBalloonAnimation(BalloonAnimation.ELASTIC)
    }
    val cropperState = createPostViewModel.imageCropper.cropState
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

    // Launch Effect
    LaunchedEffect(createPostViewModel, context) {
        createPostViewModel.resultChannel.collect { uiEvent ->
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

    // Media Launcher
    val scope = rememberCoroutineScope()
    val mediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(9),
        onResult = { uriList ->
            createPostViewModel.onEvent(
                CreatePostEvent.InputMediaContent(uriList)
            )
        }
    )

    // Camera Launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
        onResult = { bitmap ->
            bitmap?.toUri(context)?.let {
                createPostViewModel.onEvent(
                    CreatePostEvent.InputMediaContent(listOf(it))
                )
            }
        }
    )
    val cameraPermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                cameraLauncher.launch()
            } else {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = "No camera permission."
                    )
                }
            }
        }
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CreatePostTopAppBar(
                modifier = Modifier.fillMaxWidth(),
                onNavigationIconClick = { onNavigateUp() },
                actions = {
                    when (loading) {
                        true -> {
                            CircularProgressIndicator(
                                modifier = Modifier.animateContentSize(),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        false -> {
                            IconButton(
                                onClick = {
                                    createPostViewModel.onEvent(CreatePostEvent.CreatePost)
                                },
                                modifier = Modifier.animateContentSize()
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Done,
                                    contentDescription = stringResource(R.string.done)
                                )
                            }
                        }
                    }
                }
            )
        },
        bottomBar = {
            CreatePostBottomBar(
                modifier = Modifier.fillMaxWidth(),
                onMediaClick = {
                    mediaLauncher.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                },
                onCameraClick = {
                    cameraPermissionResultLauncher.launch(Manifest.permission.CAMERA)
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(SpaceSmall)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current)
                            .data(profileImageUrl)
                            .apply(
                                block = fun ImageRequest.Builder.() { crossfade(true) }
                            ).build()
                    ),
                    contentDescription = stringResource(R.string.profile_image),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .padding(SpaceSmall)
                        .size(ProfilePictureSizeSmall)
                        .clip(CircleShape)
                )

                TextField(
                    value = postText.text,
                    onValueChange = { text ->
                        createPostViewModel.onEvent(CreatePostEvent.InputPostText(text))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(text = stringResource(R.string.type_here))
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        focusedIndicatorColor = MaterialTheme.colorScheme.surface,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.surface
                    )
                )
            }

            chosenMediaUriList?.let {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(SpaceSmall),
                    contentPadding = PaddingValues(SpaceSmall)
                ) {
                    items(chosenMediaUriList) { uri ->
                        Balloon(
                            modifier = Modifier
                                .size(150.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            builder = balloonBuilder,
                            balloonContent = {
                                Box(
                                    modifier = Modifier.clickable {
                                        createPostViewModel.onEvent(
                                            CreatePostEvent.CropImage(uri, context)
                                        )
                                    },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "Modify the image.")
                                }
                            }
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(model = uri),
                                contentDescription = null,
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .fillMaxSize()
                                    .clickable {
                                        it.showAlignTop()
                                    },
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }
        }
    }
}
