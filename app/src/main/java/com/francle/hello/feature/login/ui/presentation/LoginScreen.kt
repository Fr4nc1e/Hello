package com.francle.hello.feature.login.ui.presentation

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.francle.hello.R
import com.francle.hello.core.data.util.call.AuthResult
import com.francle.hello.core.ui.hub.navigation.destination.Destination
import com.francle.hello.core.ui.theme.SpaceLarge
import com.francle.hello.core.ui.theme.SpaceMedium
import com.francle.hello.feature.login.ui.presentation.event.LoginEvent
import com.francle.hello.feature.login.ui.viewmodel.LoginViewModel
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.StyledPlayerView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    modifier: Modifier,
    snackbarHostState: SnackbarHostState,
    onNavigate: (String) -> Unit,
    onPopBackStack: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    // ViewModel Variables
    val emailTextState = viewModel.email.collectAsState().value
    val passwordTextState = viewModel.password.collectAsState().value
    val loadingState = viewModel.loading.collectAsState().value
    val isPasswordVisible = viewModel.isPasswordVisible.collectAsState().value
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
                        message = result.data ?: context.getString(R.string.fail_authorized),
                        duration = SnackbarDuration.Short
                    )
                }

                is AuthResult.UnknownError -> {
                    snackbarHostState.showSnackbar(
                        message = result.data
                            ?: context.getString(R.string.an_unknown_error_occurred),
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    AndroidView(
        factory =  {
            StyledPlayerView(context).apply {
                player = viewModel.player
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                useController = false
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
            }
        },
        modifier = Modifier.fillMaxSize()
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = SpaceMedium),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            space = SpaceMedium,
            alignment = Alignment.Bottom
        )
    ) {
        Text(
            text = stringResource(id = R.string.login),
            style = MaterialTheme.typography.titleLarge
        )
        // Email
        OutlinedTextField(
            value = emailTextState.text,
            onValueChange = {
                viewModel.onEvent(LoginEvent.InputEmail(it))
            },
            label = {
                Text(text = stringResource(id = R.string.email_address))
            },
            supportingText = {
                if (emailTextState.error != null) {
                    Text(text = emailTextState.error.error)
                }
            },
            isError = emailTextState.error != null,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            singleLine = true,
            shape = RoundedCornerShape(16.dp)
        )
        // Password
        OutlinedTextField(
            value = passwordTextState.text,
            onValueChange = {
                viewModel.onEvent(LoginEvent.InputPassword(it))
            },
            label = {
                Text(text = stringResource(id = R.string.password))
            },
            supportingText = {
                if (passwordTextState.error != null) {
                    Text(text = passwordTextState.error.error)
                }
            },
            trailingIcon = {
                IconToggleButton(
                    checked = isPasswordVisible,
                    onCheckedChange = { viewModel.onEvent(LoginEvent.PasswordVisible) }
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
                viewModel.onEvent(LoginEvent.Login)
            }),
            singleLine = true,
            shape = RoundedCornerShape(16.dp)
        )
        // Login Button
        Button(onClick = { viewModel.onEvent(LoginEvent.Login) }) {
            if (loadingState) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
            }
            Text(text = stringResource(id = R.string.login))
        }

        Divider(
            thickness = 1.dp,
            modifier = Modifier.padding(top = SpaceLarge)
        )

        // Navigate To Register Screen
        Row(
            modifier = Modifier.padding(bottom = SpaceLarge * 2),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.no_account_yet),
                style = MaterialTheme.typography.titleMedium
            )

            TextButton(onClick = { onNavigate(Destination.Register.route) }) {
                Text(text = stringResource(R.string.sign_up))
            }
        }
    }
}
