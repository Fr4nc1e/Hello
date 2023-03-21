package com.francle.hello.core.ui.util

sealed class InputError(val error: String) {
    object BlankError : InputError("Input is blank.")
    object MatchError : InputError("Input does not match the pattern.")
    object TooShortError : InputError("Input is too short.")
}
