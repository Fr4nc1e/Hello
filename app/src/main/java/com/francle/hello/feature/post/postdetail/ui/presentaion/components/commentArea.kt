package com.francle.hello.feature.post.postdetail.ui.presentaion.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.francle.hello.core.ui.hub.presentation.navigation.destination.Destination
import com.francle.hello.core.ui.theme.SpaceMedium
import com.francle.hello.feature.post.comment.domain.models.Comment
import com.francle.hello.feature.post.comment.ui.presentation.components.commentcard.CommentCard

fun LazyListScope.commentArea(
    comments: List<Comment?>,
    onLoadItems: (Int) -> Unit,
    onBottomSheetExpand: () -> Unit,
    onMediaItemClick: (Int) -> Unit,
    onCommentClick: (comment: Comment) -> Unit,
    onRepostClick: () -> Unit,
    onShareClick: () -> Unit,
    onProfileImageClick: (route: String) -> Unit
) {
    if (comments.isNotEmpty()) {
        item {
            Text(
                text = "comments:",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(SpaceMedium)
            )
        }
    }
    itemsIndexed(
        items = comments,
        key = { _: Int, item: Comment? ->
            item!!.commentId
        },
        contentType = { _: Int, item: Comment? ->
            item
        }
    ) { index, item ->
        onLoadItems(index)
        item?.also { comment ->
            CommentCard(
                modifier = Modifier.fillMaxWidth(),
                comment = comment,
                onBottomSheetExpand = { onBottomSheetExpand() },
                onMediaItemClick = { onMediaItemClick(it) },
                onCommentClick = { onCommentClick(comment) },
                onRepostClick = { onRepostClick() },
                onShareClick = { onShareClick() },
                onProfileImageClick = {
                    onProfileImageClick(
                        Destination.Profile.route + "/${comment.arrowBackUserId}"
                    )
                }
            )
        }
    }
}
