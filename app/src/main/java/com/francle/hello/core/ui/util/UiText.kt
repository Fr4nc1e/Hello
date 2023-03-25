package com.francle.hello.core.ui.util

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed class UiText {
    data class CallResponseText(val value: String) : UiText()
    data class StringResource(@StringRes val id: Int) : UiText()
}

@Composable
fun UiText.asString(): String {
    return when (this) {
        is UiText.CallResponseText -> this.value
        is UiText.StringResource -> stringResource(id = this.id)
    }
}

fun UiText.asString(context: Context): String {
    return when (this) {
        is UiText.CallResponseText -> this.value
        is UiText.StringResource -> context.getString(this.id)
    }
}
