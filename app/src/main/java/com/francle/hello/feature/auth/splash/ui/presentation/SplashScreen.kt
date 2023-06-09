package com.francle.hello.feature.auth.splash.ui.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.francle.hello.R
import com.francle.hello.feature.auth.splash.data.response.AuthResult
import com.francle.hello.feature.auth.splash.ui.viewmodel.SplashViewModel

@Composable
fun SplashScreen(
    modifier: Modifier,
    onAuthorized: () -> Unit,
    onUnAuthorized: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val loadingState = viewModel.loading.collectAsStateWithLifecycle().value

    LaunchedEffect(viewModel, context) {
        viewModel.authResults.collect { result ->
            when (result) {
                is AuthResult.Authorized -> {
                    onAuthorized()
                }
                is AuthResult.Unauthorized -> {
                    onUnAuthorized()
                }
                is AuthResult.UnknownError -> {
                    onUnAuthorized()
                }
            }
        }
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(id = R.drawable.splash_screen_image),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        if (loadingState) {
            CircularProgressIndicator()
        }
    }
}
