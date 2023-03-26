package com.francle.hello.core.ui.hub.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.francle.hello.core.ui.hub.navigation.destination.Destination
import com.francle.hello.feature.home.ui.presentation.HomeScreen
import com.francle.hello.feature.login.ui.presentation.LoginScreen
import com.francle.hello.feature.postdetail.PostDetailScreen
import com.francle.hello.feature.register.ui.presentation.RegisterScreen
import com.francle.hello.feature.splash.ui.presentation.SplashScreen

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
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
        composable(
            route = Destination.PostDetail.route + "/{postId}",
            arguments = listOf(
                navArgument(name = "postId") {
                    type = NavType.StringType
                }
            )
        ) {
            PostDetailScreen(
                modifier = modifier.fillMaxSize(),
                snackbarHostState = snackbarHostState,
                onNavigate = navHostController::navigate,
                onNavigateUp = navHostController::navigateUp
            )
        }
    }
}
