package com.francle.hello.feature.auth.register.data.request

data class RegisterRequest(
    val email: String,
    val username: String,
    val hashTag: String,
    val password: String
)
