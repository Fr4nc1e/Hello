package com.francle.hello.feature.home.ui.presentation.components.postcard.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import com.francle.hello.core.ui.hub.navigation.destination.Destination
import com.francle.hello.core.ui.theme.SpaceMedium
import com.francle.hello.core.ui.theme.SpaceSmall
import com.francle.hello.feature.home.domain.model.Post
import com.francle.hello.feature.home.ui.presentation.components.postcard.ui.components.HeadRow
import com.francle.hello.feature.home.ui.presentation.components.postcard.ui.components.PostMediaContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostCard(
    modifier: Modifier,
    post: Post,
    onNavigate: (String) -> Unit
) {
    val postContentMap = remember {
        mutableStateMapOf<String?, List<String?>?>()
    }

    post.postContentPairs?.forEach { pair ->
        val fileType = pair.fileName?.substringAfterLast(".") ?: ""
        if (postContentMap.containsKey(fileType)) {
            if (postContentMap[fileType]?.contains(pair.postContentUrl) == false) {
                postContentMap[fileType] = postContentMap[fileType]?.plus(pair.postContentUrl)
            }
        } else {
            postContentMap[fileType] = listOf(pair.postContentUrl)
        }
    }

    Card(
        onClick = { onNavigate(Destination.PostDetail.route + "/${post.id}") },
        modifier = modifier
    ) {
        HeadRow(
            modifier = Modifier.fillMaxWidth(),
            profileImageUrl = post.profileImageUrl,
            username = post.username
        )

        post.postText?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = SpaceMedium)
            )
        }

        PostMediaContent(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall)
                .align(CenterHorizontally),
            postContentMap = postContentMap
        )
    }
}
