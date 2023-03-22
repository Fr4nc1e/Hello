package com.francle.hello.feature.home.ui.presentation.components.postcard

import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.francle.hello.core.ui.hub.navigation.destination.Destination
import com.francle.hello.feature.home.domain.model.Post

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostCard(
    modifier: Modifier,
    post: Post,
    onNavigate: (String) -> Unit
) {
    Card(
        onClick = { onNavigate(Destination.PostDetail.route + "/${post.id}") },
        modifier = modifier
    ) {
        HeadRow(
            profileImageUrl = post.profileImageUrl,
            username = post.username
        )
    }
}
