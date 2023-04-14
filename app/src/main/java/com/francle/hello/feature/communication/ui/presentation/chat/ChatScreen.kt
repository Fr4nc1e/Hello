package com.francle.hello.feature.communication.ui.presentation.chat

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.francle.hello.core.ui.event.UiEvent
import com.francle.hello.core.ui.util.asString
import com.francle.hello.feature.communication.ui.presentation.event.ChatEvent
import com.francle.hello.feature.communication.ui.viewmodel.ChatViewModel
import io.getstream.chat.android.compose.ui.channels.ChannelsScreen
import io.getstream.chat.android.compose.ui.theme.ChatTheme

@Composable
fun ChatScreen(
    modifier: Modifier,
    snackbarHostState: SnackbarHostState,
    onNavigate: (String) -> Unit,
    chatViewModel: ChatViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    LaunchedEffect(chatViewModel, context) {
        chatViewModel.resultChannel.collect { uiEvent ->
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
                else -> Unit
            }
        }
    }
    
    ChatTheme(
        isInDarkMode = isSystemInDarkTheme()
    ) {
        Box(modifier = modifier) {
            ChannelsScreen(
                title = "Chat screen",
                isShowingHeader = true,
                isShowingSearch = true,
                onItemClick = { channel ->
                    chatViewModel.onEvent(ChatEvent.ClickMessageItem(channel.id))
                }
            )
        }
    }
}
