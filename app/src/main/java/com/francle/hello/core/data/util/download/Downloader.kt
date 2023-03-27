package com.francle.hello.core.data.util.download

import android.net.Uri

interface Downloader {
    fun downloadFile(
        fileName: String,
        uri: Uri
    ): Long
}
