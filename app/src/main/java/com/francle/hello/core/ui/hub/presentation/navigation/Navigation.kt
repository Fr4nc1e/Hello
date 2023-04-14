package com.francle.hello.core.ui.hub.presentation.navigation

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
import com.francle.hello.core.ui.hub.presentation.navigation.destination.Destination
import com.francle.hello.feature.auth.login.ui.presentation.LoginScreen
import com.francle.hello.feature.auth.register.ui.presentation.RegisterScreen
import com.francle.hello.feature.auth.splash.ui.presentation.SplashScreen
import com.francle.hello.feature.communication.ui.presentation.chat.ChatScreen
import com.francle.hello.feature.communication.ui.presentation.message.MessageScreen
import com.francle.hello.feature.home.ui.presentation.HomeScreen
import com.francle.hello.feature.notification.ui.presentation.NotificationScreen
import com.francle.hello.feature.pair.ui.presentation.PairScreen
import com.francle.hello.feature.post.createpost.ui.presentation.CreatePostScreen
import com.francle.hello.feature.post.fullscreen.ui.presentation.FullScreen
import com.francle.hello.feature.post.postdetail.ui.presentaion.PostDetailScreen
import com.francle.hello.feature.profile.ui.presentation.EditProfileScreen
import com.francle.hello.feature.profile.ui.presentation.ProfileScreen

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
                modifier = Modifier.fillMaxSize(),
                onAuthorized = {
                    navHostController.navigate(Destination.Home.route) {
                        popUpTo(Destination.Splash.route) {
                            inclusive = true
                        }
                    }
                },
                onUnAuthorized = {
                    navHostController.navigate(Destination.Login.route) {
                        popUpTo(Destination.Splash.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(Destination.Login.route) {
            LoginScreen(
                modifier = Modifier.fillMaxSize(),
                snackbarHostState = snackbarHostState,
                onNavigate = navHostController::navigate,
                onPopBackStack = navHostController::popBackStack
            )
        }
        composable(Destination.Register.route) {
            RegisterScreen(
                modifier = Modifier.fillMaxSize(),
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
                modifier = Modifier.fillMaxSize(),
                snackbarHostState = snackbarHostState,
                onNavigate = navHostController::navigate,
                onNavigateUp = navHostController::navigateUp
            )
        }
        composable(
            route = Destination.FullScreenView.route + "/{post}" + "/{index}",
            arguments = listOf(
                navArgument(name = "post") {
                    type = NavType.StringType
                },
                navArgument(name = "index") {
                    type = NavType.IntType
                }
            )
        ) {
            val post = it.arguments?.getString("post")
            val index = it.arguments?.getInt("index")
            if (post != null && index != null) {
                FullScreen(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHostState = snackbarHostState,
                    onNavigate = navHostController::navigate,
                    onNavigateUp = navHostController::navigateUp
                )
            }
        }
        composable(Destination.CreatePost.route) {
            CreatePostScreen(
                modifier = Modifier.fillMaxSize(),
                snackbarHostState = snackbarHostState,
                onNavigateUp = navHostController::navigateUp
            )
        }
        composable(Destination.Pair.route) {
            PairScreen(
                modifier = modifier.fillMaxSize(),
                snackbarHostState = snackbarHostState
            )
        }
        composable(Destination.Notification.route) {
            NotificationScreen(
                modifier = Modifier.fillMaxSize(),
                snackbarHostState = snackbarHostState,
                onNavigate = navHostController::navigate,
                onNavigateUp = navHostController::navigateUp
            )
        }
        composable(
            route = Destination.Profile.route + "/{userId}",
            arguments = listOf(
                navArgument(name = "userId") {
                    type = NavType.StringType
                }
            )
        ) {
            ProfileScreen(
                modifier = modifier.fillMaxSize(),
                snackbarHostState = snackbarHostState,
                onLogOut = {
                    navHostController.navigate(Destination.Login.route)
                    navHostController.graph.clear()
                },
                onNavigate = navHostController::navigate,
                onNavigateUp = navHostController::navigateUp
            )
        }
        composable(Destination.EditProfile.route) {
            EditProfileScreen(
                modifier = Modifier.fillMaxSize(),
                snackbarHostState = snackbarHostState,
                onNavigateUp = navHostController::navigateUp
            )
        }
        composable(Destination.Chat.route) {
            ChatScreen(
                modifier = modifier.fillMaxSize(),
                snackbarHostState = snackbarHostState,
                onNavigate = navHostController::navigate
            )
        }
        composable(
            route = Destination.Message.route + "/channelId",
            arguments = listOf(
                navArgument(name = "channelId") {
                    type = NavType.StringArrayType
                }
            )
        ) {
            MessageScreen(
                snackbarHostState = snackbarHostState,
                onNavigate = navHostController::navigate,
                onNavigateUp = navHostController::navigateUp
            )
        }
    }
}
