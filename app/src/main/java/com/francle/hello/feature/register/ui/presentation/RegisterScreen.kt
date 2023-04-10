package com.francle.hello.feature.register.ui.presentation

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.francle.hello.R
import com.francle.hello.core.data.util.call.AuthResult
import com.francle.hello.core.ui.hub.navigation.destination.Destination
import com.francle.hello.core.ui.theme.SpaceMedium
import com.francle.hello.core.ui.theme.SpaceSmall
import com.francle.hello.feature.register.ui.presentation.event.RegisterEvent
import com.francle.hello.feature.register.ui.viewmodel.RegisterViewModel
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.StyledPlayerView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    modifier: Modifier,
    snackbarHostState: SnackbarHostState,
    onNavigate: (String) -> Unit,
    onPopBackStack: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    // State
    val emailTextState = viewModel.email.collectAsState().value
    val usernameTextState = viewModel.username.collectAsState().value
    val hashTagTextState = viewModel.hashTag.collectAsState().value
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

    AndroidView(
        factory = {
            StyledPlayerView(context).apply {
                player = viewModel.player
                useController = false
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
            }
        },
        modifier = modifier
    )

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(id = R.string.register),
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(SpaceMedium))
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
            singleLine = true,
            shape = RoundedCornerShape(16.dp)
        )
        Spacer(modifier = Modifier.height(SpaceMedium))
        // Username
        OutlinedTextField(
            value = usernameTextState.text,
            onValueChange = {
                viewModel.onEvent(RegisterEvent.InputUsername(it))
            },
            label = {
                Text(text = stringResource(R.string.username))
            },
            supportingText = {
                if (usernameTextState.error != null) {
                    Text(text = usernameTextState.error.error)
                }
            },
            isError = usernameTextState.error != null,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            singleLine = true,
            shape = RoundedCornerShape(16.dp)
        )
        Spacer(modifier = Modifier.height(SpaceMedium))
        // HashTag
        OutlinedTextField(
            value = hashTagTextState.text,
            onValueChange = {
                viewModel.onEvent(RegisterEvent.InputHashTag(it))
            },
            label = {
                Text(text = stringResource(R.string.id))
            },
            supportingText = {
                if (hashTagTextState.error != null) {
                    Text(text = hashTagTextState.error.error)
                }
            },
            isError = hashTagTextState.error != null,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            singleLine = true,
            shape = RoundedCornerShape(16.dp)
        )
        Spacer(modifier = Modifier.height(SpaceMedium))
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
            singleLine = true,
            shape = RoundedCornerShape(16.dp)
        )
        Spacer(modifier = Modifier.height(SpaceMedium))
        // Register Button
        Button(onClick = { viewModel.onEvent(RegisterEvent.Register) }) {
            if (loadingState) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(SpaceSmall))
            }
            Text(text = stringResource(id = R.string.register))
        }
    }
}
