package com.francle.hello.feature.pair.ui.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.francle.hello.core.ui.event.UiEvent
import com.francle.hello.core.ui.theme.SpaceLarge
import com.francle.hello.core.ui.theme.SpaceSmall
import com.francle.hello.core.ui.util.asString
import com.francle.hello.feature.pair.ui.event.PairEvent
import com.francle.hello.feature.pair.ui.presentation.components.ActionButton
import com.francle.hello.feature.pair.ui.presentation.components.PairRow
import com.francle.hello.feature.pair.ui.presentation.components.PairUserInfo
import com.francle.hello.feature.pair.ui.viewmodel.PairViewModel

@Composable
fun PairScreen(
    modifier: Modifier,
    snackbarHostState: SnackbarHostState,
    pairViewModel: PairViewModel = hiltViewModel()
) {
    // ViewModel State
    val pairUser = pairViewModel.pairUser.collectAsState().value
    val profileImageUrl = pairViewModel.profileImageUrl.collectAsState().value
    val loading = pairViewModel.loading.collectAsState().value

    // Local Variables
    val context = LocalContext.current

    LaunchedEffect(pairViewModel, context) {
        pairViewModel.resultChannel.collect {
            when (it) {
                is UiEvent.Message -> {
                    snackbarHostState.showSnackbar(
                        message = it.message.asString(context),
                        duration = SnackbarDuration.Short
                    )
                }
                is UiEvent.Navigate -> {}
                UiEvent.NavigateUp -> {}
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Loading till pair a user
        if (loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        // Self status at the top of the screen
        PairRow(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .align(Alignment.TopCenter),
            profileImageUrl = profileImageUrl,
            onPairClick = { pairViewModel.onEvent(PairEvent.Pair) }
        )

        // PairUser information at the center of the screen
        pairUser?.also { pairUser ->
            Image(
                painter = rememberAsyncImagePainter(
                    model = pairUser.bannerImageUrl,
                    contentScale = ContentScale.FillBounds
                ),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }

        //User info at the bottom start of the screen
        pairUser?.also { pairUser ->
            PairUserInfo(
                modifier = Modifier
                    .padding(SpaceLarge)
                    .align(Alignment.BottomStart),
                profileImageUrl = pairUser.profileImageUrl,
                username = pairUser.username,
                age = pairUser.age
            )
        }

        // Action buttons at bottom end of the screen
        ActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(SpaceSmall),
            onLikeClick = {},
            onDislikeClick = {}
        )
    }
}
