package com.francle.hello.core.ui.hub

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.francle.hello.core.ui.hub.navigation.Navigation
import com.francle.hello.core.ui.hub.navigation.NavigationBottomBar
import com.francle.hello.core.ui.hub.viewmodel.AppHubViewModel

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
    val curRoute = viewModel.curRoute.collectAsState().value

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
                    onNavigate = navHostController::navigate,
                    onPopBackStack = navHostController::popBackStack
                )
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) {
                Snackbar(snackbarData = it)
            }
        }
    ) {
        Navigation(
            modifier = Modifier.padding(it),
            navHostController = navHostController,
            snackbarHostState = snackbarHostState
        )
    }
}
