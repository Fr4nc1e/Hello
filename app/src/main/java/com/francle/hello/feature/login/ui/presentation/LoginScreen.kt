package com.francle.hello.feature.login.ui.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun LoginScreen(
    modifier: Modifier,
    snackbarHostState: SnackbarHostState,
    onNavigate: (String) -> Unit
) {
    Column(modifier) {
        Text(text = "login")
    }
}
