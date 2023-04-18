package com.francle.hello.feature.post.comment.ui.presentation.components.commentcard.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.francle.hello.R
import com.francle.hello.core.ui.theme.SpaceSmall

@Composable
fun CommentMediaContent(
    modifier: Modifier,
    mediaUrls: List<String>?,
    onMediaItemClick: (Int) -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        mediaUrls?.chunked(3)?.forEach { chunkedUrls ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                chunkedUrls.forEach {
                    Box(
                        modifier = Modifier
                            .weight(1f, false)
                            .padding(SpaceSmall),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(it),
                            contentDescription = stringResource(R.string.image_content),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .aspectRatio(1f)
                                .animateContentSize()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    onMediaItemClick(mediaUrls.indexOf(it))
                                }
                        )
                    }
                }
            }
        }
    }
}
