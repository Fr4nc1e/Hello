package com.francle.hello.feature.home.ui.presentation.components.postcard.ui.components

import android.net.Uri
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImage
import com.francle.hello.R
import com.francle.hello.core.di.CoreModule
import com.francle.hello.core.domain.model.VideoItem
import com.francle.hello.core.ui.theme.IconSizeLarge
import com.francle.hello.core.ui.theme.SpaceSmall
import com.google.android.exoplayer2.MediaItem

@Composable
fun PostMediaContent(
    modifier: Modifier,
    postContentMap: Map<String?, List<String?>?>
) {
    val retriever = CoreModule.provideRetriever()
    val mediaItems = mutableListOf<Any>()
    postContentMap.forEach { (key, values) ->
        when (key) {
            in listOf("png", "jpg", "jpeg") -> {
                values?.let { list ->
                    mediaItems.addAll(list.filterNotNull())
                }
            }
            "mp4" -> {
                mediaItems.addAll(
                    values?.map { url ->
                        val uri = Uri.parse(url)
                        VideoItem(
                            contentUri = uri,
                            mediaItem = MediaItem.fromUri(uri)
                        )
                    }.orEmpty()
                )
            }
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        mediaItems.chunked(3).forEach { chunkedItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = CenterVertically
            ) {
                chunkedItems.forEach {
                    when (it) {
                        is String -> {
                            Box(
                                modifier = Modifier
                                    .weight(1f, false)
                                    .padding(SpaceSmall),
                                contentAlignment = Center
                            ) {
                                AsyncImage(
                                    model = it,
                                    contentDescription = stringResource(R.string.image_content),
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .aspectRatio(1f)
                                        .animateContentSize()
                                )
                            }
                        }

                        is VideoItem -> {
                            Box(
                                modifier = Modifier
                                    .weight(1f, false)
                                    .padding(SpaceSmall),
                                contentAlignment = Center
                            ) {
                                retriever.setDataSource(it.contentUri.toString())
                                val bitmap = retriever.getFrameAtTime(0)
                                bitmap?.asImageBitmap()?.let { imageBitmap ->
                                    Image(
                                        bitmap = imageBitmap,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .aspectRatio(1f)
                                            .animateContentSize()
                                            .clickable { /* TODO: Go to full screen video */ }
                                    )
                                }
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
