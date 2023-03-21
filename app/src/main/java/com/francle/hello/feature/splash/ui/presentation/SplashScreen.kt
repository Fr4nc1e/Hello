package com.francle.hello.feature.splash.ui.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.francle.hello.R
import com.francle.hello.core.data.util.AuthResult
import com.francle.hello.core.ui.hub.navigation.destination.Destination
import com.francle.hello.feature.splash.ui.viewmodel.SplashViewModel

@Composable
fun SplashScreen(
    modifier: Modifier,
    snackbarHostState: SnackbarHostState,
    onNavigate: (String) -> Unit,
    onPopBackStack: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    LaunchedEffect(viewModel) {
        viewModel.authResults.collect { result ->
            when (result) {
                is AuthResult.Authorized -> {
                    onPopBackStack()
                    onNavigate(Destination.Home.route)
                }
                is AuthResult.Unauthorized -> {
                    snackbarHostState.showSnackbar(
                        message = "You're not authorized",
                        duration = SnackbarDuration.Short
                    )
                }
                is AuthResult.UnknownError -> {
                    snackbarHostState.showSnackbar(
                        message = "An unknown error occurred",
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        when (isSystemInDarkTheme()) {
            true -> {
                Image(
                    painter = painterResource(id = R.mipmap.ic_launcher_night_foreground),
                    contentDescription = stringResource(R.string.night_logo)
                )
            }
            false -> {
                Image(
                    painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                    contentDescription = stringResource(R.string.logo)
                )
            }
        }
    }
}
