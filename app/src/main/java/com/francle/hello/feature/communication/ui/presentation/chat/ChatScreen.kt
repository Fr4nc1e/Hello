package com.francle.hello.feature.communication.ui.presentation.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.francle.hello.R
import com.francle.hello.core.ui.event.UiEvent
import com.francle.hello.core.ui.theme.SpaceSmall
import com.francle.hello.core.ui.util.asString
import com.francle.hello.feature.communication.ui.presentation.event.ChatEvent
import com.francle.hello.feature.communication.ui.viewmodel.ChatViewModel
import com.getstream.sdk.chat.utils.DateFormatter
import io.getstream.chat.android.compose.ui.channels.header.ChannelListHeader
import io.getstream.chat.android.compose.ui.channels.info.SelectedChannelMenu
import io.getstream.chat.android.compose.ui.channels.list.ChannelList
import io.getstream.chat.android.compose.ui.components.SearchInput
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.compose.viewmodel.channels.ChannelListViewModel

@Composable
fun ChatScreen(
    modifier: Modifier,
    snackbarHostState: SnackbarHostState,
    onNavigate: (String) -> Unit,
    chatViewModel: ChatViewModel = hiltViewModel()
) {
    val factory = chatViewModel.factory
    val channelListViewModel = viewModel(
        modelClass = ChannelListViewModel::class.java,
        factory = factory
    )
    val clickHeaderMenu by chatViewModel.clickHeaderMenu.collectAsStateWithLifecycle()
    val query by chatViewModel.query.collectAsStateWithLifecycle()
    val user by channelListViewModel.user.collectAsStateWithLifecycle()
    val delegatedSelectedChannel by channelListViewModel.selectedChannel
    val connectionState by channelListViewModel.connectionState.collectAsStateWithLifecycle()
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
        isInDarkMode = isSystemInDarkTheme(),
        dateFormatter = DateFormatter.from(context)
    ) {
        Box(modifier = modifier.fillMaxSize()) {
            Column {
                ChannelListHeader(
                    title = stringResource(id = R.string.app_name),
                    currentUser = user,
                    connectionState = connectionState,
                    color = MaterialTheme.colorScheme.primary,
                    trailingContent = {
                        IconButton(onClick = { chatViewModel.onEvent(ChatEvent.ClickHeaderMenu) }) {
                            Icon(imageVector = Icons.Filled.MoreVert, contentDescription = null)
                        }
                        DropdownMenu(
                            expanded = clickHeaderMenu,
                            onDismissRequest = { chatViewModel.onEvent(ChatEvent.ClickHeaderMenu) },
                            offset = DpOffset(x = 240.dp, y = 0.dp)
                        ) {
                            DropdownMenuItem(
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Filled.AddCircleOutline,
                                        contentDescription = null
                                    )
                                },
                                text = {
                                    Text(text = "Add a user")
                                },
                                onClick = {}
                            )

                            DropdownMenuItem(
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Filled.Create,
                                        contentDescription = null
                                    )
                                },
                                text = {
                                    Text(text = "Create a channel")
                                },
                                onClick = {}
                            )
                        }
                    }
                )

                SearchInput(
                    modifier = Modifier
                        .background(color = MaterialTheme.colorScheme.primary)
                        .fillMaxWidth()
                        .height(60.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .padding(SpaceSmall),
                    query = query,
                    onValueChange = {
                        chatViewModel.onEvent(ChatEvent.InputQuery(it))
                        channelListViewModel.setSearchQuery(it)
                    }
                )

                ChannelList(
                    modifier = Modifier.fillMaxSize(),
                    viewModel = channelListViewModel,
                    onChannelClick = {
                        chatViewModel.onEvent(ChatEvent.ClickMessageItem(it.cid))
                    },
                    onChannelLongClick = { channelListViewModel.selectChannel(it) }
                )
            }

            val selectedChannel = delegatedSelectedChannel
            if (selectedChannel != null) {
                SelectedChannelMenu(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .align(Alignment.Center),
                    shape = RoundedCornerShape(16.dp),
                    isMuted = channelListViewModel.isChannelMuted(selectedChannel.cid),
                    selectedChannel = selectedChannel,
                    currentUser = user,
                    onChannelOptionClick = { action ->
                        channelListViewModel.performChannelAction(action)
                    },
                    onDismiss = { channelListViewModel.dismissChannelAction() }
                )
            }
        }
    }
}
