package com.francle.hello.feature.communication.ui.presentation.message

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.francle.hello.core.ui.event.UiEvent
import com.francle.hello.core.ui.util.asString
import com.francle.hello.core.util.Constants
import com.francle.hello.feature.communication.ui.viewmodel.MessageViewModel
import io.getstream.chat.android.compose.ui.messages.MessagesScreen

@Composable
fun MessageScreen(
    snackbarHostState: SnackbarHostState,
    onNavigate: (String) -> Unit,
    onNavigateUp: () -> Unit,
    messageViewModel: MessageViewModel = hiltViewModel()
) {
    val channelId = messageViewModel.channelId.collectAsStateWithLifecycle().value
    val context = LocalContext.current

    LaunchedEffect(messageViewModel, context) {
        messageViewModel.resultChannel.collect { uiEvent ->
            when (uiEvent) {
                is UiEvent.Message -> {
                    snackbarHostState.showSnackbar(
                        message = uiEvent.message.asString(context),
                        duration = SnackbarDuration.Short
                    )
                }
                is UiEvent.Navigate -> {}
                else -> Unit
            }
        }
    }

    MessagesScreen(
        channelId = channelId,
        messageLimit = Constants.PAGE_SIZE,
        onBackPressed = onNavigateUp
    )
}
