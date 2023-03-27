package com.francle.hello.core.data.util.call

sealed class AuthResult<T>(val data: T? = null) {
    class Authorized<T>(data: T? = null) : AuthResult<T>(data)
    class Unauthorized<T>(data: T? = null) : AuthResult<T>(data)
    class UnknownError<T>(data: T? = null) : AuthResult<T>(data)
}
