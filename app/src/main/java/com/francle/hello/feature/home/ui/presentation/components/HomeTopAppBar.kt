package com.francle.hello.feature.home.ui.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.francle.hello.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
    onProfileImageClick: () -> Unit,
    onNotificationClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    CenterAlignedTopAppBar(
        title = {
            Text(text = stringResource(id = R.string.home))
        },
        navigationIcon = {
            IconButton(onClick = { onProfileImageClick() }) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                )
            }
        },
        actions = {
            IconButton(onClick = { onNotificationClick() }) {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = null
                )
            }
        },
        scrollBehavior = scrollBehavior
    )
}