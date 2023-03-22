package com.francle.hello.feature.register.ui.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.francle.hello.R
import com.francle.hello.core.data.util.AuthResult
import com.francle.hello.core.ui.hub.navigation.destination.Destination
import com.francle.hello.feature.register.ui.presentation.event.RegisterEvent
import com.francle.hello.feature.register.ui.viewmodel.RegisterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    modifier: Modifier,
    snackbarHostState: SnackbarHostState,
    onNavigate: (String) -> Unit,
    onPopBackStack: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val emailTextState = viewModel.email.collectAsState().value
    val usernameTextState = viewModel.username.collectAsState().value
    val passwordTextState = viewModel.password.collectAsState().value
    val loadingState = viewModel.loading.collectAsState().value
    val isPasswordVisible = viewModel.isPasswordVisible.collectAsState().value
    val context = LocalContext.current

    LaunchedEffect(viewModel, context) {
        viewModel.authResults.collect { result ->
            when (result) {
                is AuthResult.Authorized -> {
                    onPopBackStack()
                    onNavigate(Destination.Login.route)
                }
                is AuthResult.UnknownError -> {
                    snackbarHostState.showSnackbar(
                        message = result.data
                            ?: context.getString(R.string.an_unknown_error_occurred),
                        duration = SnackbarDuration.Short
                    )
                }
                else -> {}
            }
        }
    }

    Surface(
        modifier = modifier
    ) {
        if (loadingState) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.register),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(20.dp))
            // Email
            OutlinedTextField(
                value = emailTextState.text,
                onValueChange = {
                    viewModel.onEvent(RegisterEvent.InputEmail(it))
                },
                label = {
                    Text(text = stringResource(R.string.email_address))
                },
                supportingText = {
                    if (emailTextState.error != null) {
                        Text(text = emailTextState.error.error)
                    }
                },
                isError = emailTextState.error != null,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(20.dp))
            // Username
            OutlinedTextField(
                value = usernameTextState.text,
                onValueChange = {
                    viewModel.onEvent(RegisterEvent.InputUsername(it))
                },
                label = {
                    Text(text = stringResource(R.string.username))
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(20.dp))
            // Password
            OutlinedTextField(
                value = passwordTextState.text,
                onValueChange = {
                    viewModel.onEvent(RegisterEvent.InputPassword(it))
                },
                label = {
                    Text(text = stringResource(R.string.password))
                },
                supportingText = {
                    if (passwordTextState.error != null) {
                        Text(text = passwordTextState.error.error)
                    }
                },
                trailingIcon = {
                    IconToggleButton(
                        checked = isPasswordVisible,
                        onCheckedChange = { viewModel.onEvent(RegisterEvent.PasswordVisible) }
                    ) {
                        when (isPasswordVisible) {
                            true -> {
                                Icon(
                                    imageVector = Icons.Filled.Visibility,
                                    contentDescription = stringResource(
                                        R.string.password_visibility
                                    )
                                )
                            }
                            false -> {
                                Icon(
                                    imageVector = Icons.Filled.VisibilityOff,
                                    contentDescription = stringResource(
                                        R.string.password_visibility
                                    )
                                )
                            }
                        }
                    }
                },
                isError = passwordTextState.error != null,
                visualTransformation = when (isPasswordVisible) {
                    true -> {
                        VisualTransformation.None
                    }
                    false -> {
                        PasswordVisualTransformation()
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    viewModel.onEvent(RegisterEvent.Register)
                }),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(20.dp))
            // Login Button
            OutlinedButton(onClick = { viewModel.onEvent(RegisterEvent.Register) }) {
                Text(text = stringResource(id = R.string.register))
            }
        }
    }
}
