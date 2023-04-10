package com.francle.hello.core.ui.hub.presentation.navigation.util

import com.google.gson.Gson
import java.net.URLEncoder

fun <T> String.fromJson(type: Class<T>): T {
    return Gson().fromJson(this, type)
}

fun <T> T.toJson(): String? {
    return Gson().toJson(this)
}

fun String.urlEncode(): String? = URLEncoder.encode(this, "utf-8")
