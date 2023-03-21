package com.francle.hello.core.ui.util

data class TextState(
    val text: String = "",
    val error: InputError? = null,
    val maxLetters: Int = Int.MAX_VALUE,
    val maxLines: Int = Int.MAX_VALUE
)
