package com.francle.hello.feature.home.ui.presentation.components.postcard.ui.components

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.francle.hello.R
import com.francle.hello.core.ui.theme.IconSizeLarge
import com.francle.hello.core.ui.theme.SpaceSmall
import com.francle.hello.feature.home.domain.model.PostContentPair
import com.francle.hello.feature.home.ui.viewmodel.HomeViewModel

@Composable
fun PostMediaContent(
    modifier: Modifier,
    postContentPairs: List<PostContentPair>?,
    onMediaItemClick: (Int) -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val retriever = homeViewModel.retriever
    Column(
        modifier = modifier,
        horizontalAlignment = CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        postContentPairs?.chunked(3)?.forEach { chunkedItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = CenterVertically
            ) {
                chunkedItems.forEach {
                    when (it.fileName?.substringAfterLast(".")) {
                        "png", "jpg", "jpeg" -> {
                            Box(
                                modifier = Modifier
                                    .weight(1f, false)
                                    .padding(SpaceSmall),
                                contentAlignment = Center
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(it.postContentUrl),
                                    contentDescription = stringResource(R.string.image_content),
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .aspectRatio(1f)
                                        .animateContentSize()
                                        .clip(RoundedCornerShape(16.dp))
                                        .clickable {
                                            onMediaItemClick(postContentPairs.indexOf(it))
                                        }
                                )
                            }
                        }

                        "mp4" -> {
                            Box(
                                modifier = Modifier
                                    .weight(1f, false)
                                    .padding(SpaceSmall),
                                contentAlignment = Center
                            ) {
                                val uri = it.postContentUrl?.toUri()
                                retriever.setDataSource(uri.toString())
                                val bitmap = retriever.getFrameAtTime(0)
                                Image(
                                    painter = rememberAsyncImagePainter(bitmap),
                                    contentDescription = stringResource(
                                        id = R.string.video_content
                                    ),
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .aspectRatio(1f)
                                        .animateContentSize()
                                        .clip(RoundedCornerShape(16.dp))
                                        .clickable {
                                            onMediaItemClick(postContentPairs.indexOf(it))
                                        }
                                )
                                Icon(
                                    imageVector = Icons.Filled.PlayCircle,
                                    modifier = Modifier.size(IconSizeLarge),
                                    contentDescription = stringResource(
                                        id = R.string.play_video
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
