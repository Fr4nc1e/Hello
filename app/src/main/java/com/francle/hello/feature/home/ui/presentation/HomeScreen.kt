package com.francle.hello.feature.home.ui.presentation

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.francle.hello.R
import com.francle.hello.core.ui.hub.navigation.destination.Destination
import com.francle.hello.core.ui.theme.SpaceSmall
import com.francle.hello.feature.home.ui.presentation.components.postcard.ui.PostCard
import com.francle.hello.feature.home.ui.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier,
    snackbarHostState: SnackbarHostState,
    onNavigate: (String) -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val posts = homeViewModel.posts.collectAsState().value
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) {
        CenterAlignedTopAppBar(
            title = {
                Text(text = stringResource(id = R.string.home))
            },
            scrollBehavior = scrollBehavior
        )

        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(scrollState)
        ) {
            posts.forEach { post ->
                post?.let {
                    PostCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(SpaceSmall)
                            .animateContentSize()
                            .clickable { onNavigate(Destination.PostDetail.route + "/${it.id}") },
                        post = it,
                        onNavigate = onNavigate
                    )
                }
            }
        }
    }
}
