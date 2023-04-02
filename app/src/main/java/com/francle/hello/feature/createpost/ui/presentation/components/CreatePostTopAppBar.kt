package com.francle.hello.feature.createpost.ui.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.francle.hello.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostTopAppBar(
    modifier: Modifier,
    onNavigationIconClick: () -> Unit,
    actions: @Composable () -> Unit
) {
    CenterAlignedTopAppBar(
        title = { Text(text = stringResource(id = R.string.create_post)) },
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = { onNavigationIconClick() }) {
                Icon(
                    imageVector = Icons.Outlined.Cancel,
                    contentDescription = stringResource(R.string.back)
                )
            }
        },
        actions = {
            actions()
        }
    )
}