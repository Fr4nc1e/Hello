package com.francle.hello.core.ui.util

sealed class InputError(val error: String) {
    object BlankError : InputError("Input is blank.")
    object EmailMatchError : InputError("Email does not match the pattern.")
    object HashTagMatchError : InputError("Id must start with '@'.")
    object TooShortError : InputError("Input is too short.")
}
