package com.francle.hello.feature.profile.ui.presentation.components

import androidx.compose.animation.animateContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileTopAppBar(
    modifier: Modifier,
    title: String,
    loading: Boolean,
    onNavigateBackClick: () -> Unit,
    onCompletedClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = { 
            Text(text = title) 
        },
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = { onNavigateBackClick() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = null
                )
            }
        },
        actions = {
            when (loading) {
                true -> {
                    CircularProgressIndicator(
                        modifier = Modifier.animateContentSize(),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                false -> {
                    IconButton(onClick = { onCompletedClick() }) {
                        Icon(imageVector = Icons.Filled.Done, contentDescription = null)
                    }
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}
