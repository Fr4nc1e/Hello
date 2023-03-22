package com.francle.hello.core.ui.hub.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.francle.hello.core.ui.hub.navigation.destination.Destination
import com.francle.hello.feature.home.ui.presentation.HomeScreen
import com.francle.hello.feature.login.ui.presentation.LoginScreen
import com.francle.hello.feature.register.ui.presentation.RegisterScreen
import com.francle.hello.feature.splash.ui.presentation.SplashScreen

@Composable
fun Navigation(
    modifier: Modifier,
    navHostController: NavHostController,
    snackbarHostState: SnackbarHostState
) {
    NavHost(
        navController = navHostController,
        startDestination = Destination.Splash.route
    ) {
        composable(Destination.Splash.route) {
            SplashScreen(
                modifier = modifier.fillMaxSize(),
                onNavigate = navHostController::navigate,
                onPopBackStack = navHostController::popBackStack
            )
        }
        composable(Destination.Login.route) {
            LoginScreen(
                modifier = modifier.fillMaxSize(),
                snackbarHostState = snackbarHostState,
                onNavigate = navHostController::navigate,
                onPopBackStack = navHostController::popBackStack
            )
        }
        composable(Destination.Register.route) {
            RegisterScreen(
                modifier = modifier.fillMaxSize(),
                snackbarHostState = snackbarHostState,
                onNavigate = navHostController::navigate,
                onPopBackStack = navHostController::popBackStack
            )
        }
        composable(Destination.Home.route) {
            HomeScreen(
                modifier = modifier.fillMaxSize(),
                snackbarHostState = snackbarHostState,
                onNavigate = navHostController::navigate
            )
        }
    }
}
