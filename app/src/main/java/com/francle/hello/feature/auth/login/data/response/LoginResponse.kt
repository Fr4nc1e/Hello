package com.francle.hello.feature.auth.login.data.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("userId")
    val userId: String,
    @SerializedName("token")
    val token: String
)
