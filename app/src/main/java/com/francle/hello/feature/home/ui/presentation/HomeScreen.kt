package com.francle.hello.feature.home.ui.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun HomeScreen(
    modifier: Modifier,
    snackbarHostState: SnackbarHostState,
    onNavigate: (String) -> Unit
) {
    Column(modifier = modifier) {
        Text(text = "home")
    }
}
