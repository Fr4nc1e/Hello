package com.francle.hello.core.ui.hub.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.francle.hello.R
import com.francle.hello.core.ui.hub.presentation.navigation.Navigation
import com.francle.hello.core.ui.hub.presentation.navigation.NavigationBottomBar
import com.francle.hello.core.ui.hub.presentation.navigation.destination.Destination
import com.francle.hello.core.ui.hub.viewmodel.AppHubViewModel

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppHub(
    modifier: Modifier,
    viewModel: AppHubViewModel = hiltViewModel()
) {
    val navHostController = rememberNavController()
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val curRoute = viewModel.curRoute.collectAsStateWithLifecycle().value

    LaunchedEffect(navHostController) {
        navHostController.currentBackStackEntryFlow.collect { backStackEntry ->
            backStackEntry.destination.route?.let { route ->
                viewModel.getCurRoute(route)
            }
        }
    }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            if (viewModel.inList()) {
                NavigationBottomBar(
                    modifier = Modifier.fillMaxWidth(),
                    curRoute = curRoute,
                    navHostController = navHostController
                )
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) {
                Snackbar(snackbarData = it)
            }
        },
        floatingActionButton = {
            if (curRoute == Destination.Home.route) {
                FloatingActionButton(
                    onClick = {
                        navHostController.navigate(Destination.CreatePost.route)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.AddCircle,
                        contentDescription = stringResource(R.string.create_post)
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) {
        Navigation(
            modifier = Modifier.padding(it),
            navHostController = navHostController,
            snackbarHostState = snackbarHostState
        )
    }
}
