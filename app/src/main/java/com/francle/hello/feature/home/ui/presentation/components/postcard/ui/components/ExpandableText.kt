package com.francle.hello.feature.home.ui.presentation.components.postcard.ui.components


import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.francle.hello.R
import com.francle.hello.core.util.Constants

@Composable
fun ExpandableText(
    text: String,
    modifier: Modifier = Modifier,
    collapsedMaxLine: Int = Constants.MAX_POST_DESCRIPTION_LINES
) {
    var isExpanded by remember { mutableStateOf(false) }
    var clickable by remember { mutableStateOf(false) }
    var lastCharIndex by remember { mutableStateOf(0) }

    Box(
        modifier = modifier
            .animateContentSize(
                animationSpec = SpringSpec(dampingRatio = Spring.DampingRatioMediumBouncy)
            )
            .clickable(clickable) {
                isExpanded = !isExpanded
            }
    ) {
        Text(
            text = buildAnnotatedString {
                val showMoreText = stringResource(id = R.string.show_more)
                val showLessText = stringResource(id = R.string.show_less)
                if (clickable) {
                    if (isExpanded) {
                        append(text)
                        withStyle(
                            SpanStyle(
                                color = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            append(" ")
                            append(showLessText)
                        }
                    } else {
                        val adjustText = text
                            .substring(
                                startIndex = 0,
                                endIndex = lastCharIndex
                            )
                            .dropLast(showMoreText.length)
                            .dropLastWhile { Character.isWhitespace(it) || it == '.' }
                        append(adjustText)
                        withStyle(
                            SpanStyle(
                                color = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            append(" ")
                            append(showMoreText)
                        }
                    }
                } else {
                    append(text)
                }
            },
            style = MaterialTheme.typography.titleMedium,
            maxLines = if (isExpanded) Int.MAX_VALUE else collapsedMaxLine,
            onTextLayout = {
                if (!isExpanded && it.hasVisualOverflow) {
                    clickable = true
                    lastCharIndex = it.getLineEnd(collapsedMaxLine - 1)
                }
            }
        )
    }
}
