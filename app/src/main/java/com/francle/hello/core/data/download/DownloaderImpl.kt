package com.francle.hello.core.data.download

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment

class DownloaderImpl(
    context: Context
) : Downloader {
    private val downloadManager = context.getSystemService(DownloadManager::class.java)
    private val contentResolver = context.contentResolver
    override fun downloadFile(
        fileName: String,
        uri: Uri
    ): Long {
        val request = DownloadManager.Request(uri)
            .setMimeType(contentResolver.getType(uri))
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setTitle(fileName)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
        return downloadManager.enqueue(request)
    }
}
