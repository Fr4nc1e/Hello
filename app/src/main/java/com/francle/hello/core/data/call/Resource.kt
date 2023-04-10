package com.francle.hello.core.data.call

import com.francle.hello.core.ui.util.UiText

sealed class Resource<T>(
    val data: T? = null,
    val message: UiText? = null
) {
    class Success<T>(data: T? = null, message: UiText? = null) : Resource<T>(data, message)
    class Error<T>(data: T? = null, message: UiText? = null) : Resource<T>(data, message)
}
