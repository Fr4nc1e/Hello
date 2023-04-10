package com.francle.hello.feature.login.data.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("userId")
    val userId: String,
    @SerializedName("token")
    val token: String
)
