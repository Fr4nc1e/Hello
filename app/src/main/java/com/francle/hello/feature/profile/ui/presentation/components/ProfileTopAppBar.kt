package com.francle.hello.feature.profile.ui.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.ModeEdit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.francle.hello.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTopAppBar(
    modifier: Modifier,
    showDropMenu: Boolean,
    isOwnProfile: Boolean,
    scrollBehavior: TopAppBarScrollBehavior,
    onNavigationIconClick: () -> Unit,
    onClickMoreVert: () -> Unit,
    onClickLogOut: () -> Unit,
    onEditClick: () -> Unit,
    onMessageClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Text(text = stringResource(id = R.string.profile))
        },
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = { onNavigationIconClick() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back)
                )
            }
        },
        actions = {
            if (!isOwnProfile) {
                IconButton(onClick = { onMessageClick() }) {
                    Icon(
                        imageVector = Icons.Filled.Message,
                        contentDescription = null
                    )
                }
            }
            IconButton(onClick = { onClickMoreVert() }) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = null
                )
            }
            DropdownMenu(
                expanded = showDropMenu,
                onDismissRequest = { onClickMoreVert() }
            ) {
                if (isOwnProfile) {
                    DropdownMenuItem(
                        text = { Text(text = stringResource(R.string.log_out)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.ExitToApp,
                                contentDescription = stringResource(id = R.string.log_out)
                            )
                        },
                        onClick = { onClickLogOut() }
                    )

                    DropdownMenuItem(
                        text = { Text(text = "Edit profile") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Filled.ModeEdit, contentDescription = null)
                        },
                        onClick = { onEditClick() }
                    )
                }
            }
        },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}
