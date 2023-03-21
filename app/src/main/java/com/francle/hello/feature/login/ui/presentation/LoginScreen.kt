package com.francle.hello.feature.login.ui.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.francle.hello.R
import com.francle.hello.core.data.util.AuthResult
import com.francle.hello.core.ui.hub.navigation.destination.Destination
import com.francle.hello.feature.home.event.LoginEvent
import com.francle.hello.feature.login.ui.viewmodel.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    modifier: Modifier,
    snackbarHostState: SnackbarHostState,
    onNavigate: (String) -> Unit,
    onPopBackStack: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val emailTextState = viewModel.email.collectAsState().value
    val passwordTextState = viewModel.password.collectAsState().value
    val loadingState = viewModel.loading.collectAsState().value
    val context = LocalContext.current

    LaunchedEffect(viewModel, context) {
        viewModel.authResults.collect { result ->
            when (result) {
                is AuthResult.Authorized -> {
                    onPopBackStack()
                    onNavigate(Destination.Home.route)
                }
                is AuthResult.Unauthorized -> {
                    snackbarHostState.showSnackbar(
                        message = context.getString(R.string.fail_authorized),
                        duration = SnackbarDuration.Short
                    )
                }
                is AuthResult.UnknownError -> {
                    snackbarHostState.showSnackbar(
                        message = context.getString(R.string.an_unknown_error_occurred),
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    if (loadingState) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Email
        TextField(
            value = emailTextState.text,
            onValueChange = {
                viewModel.onEvent(LoginEvent.InputEmail(it))
            }
        )
        Spacer(modifier = Modifier.height(20.dp))
        // Password
        TextField(
            value = passwordTextState.text,
            onValueChange = {
                viewModel.onEvent(LoginEvent.InputPassword(it))
            }
        )
        Spacer(modifier = Modifier.height(20.dp))
        // Login Button
        Button(onClick = { viewModel.onEvent(LoginEvent.Login) }) {
            Text(text = stringResource(id = R.string.login))
        }
    }
}
