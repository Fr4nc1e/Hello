package com.francle.hello.core.data.file

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import java.io.IOException
import java.util.Locale

fun ContentResolver.getFileName(
    uri: Uri
): String {
    val returnCursor = query(
        uri,
        null,
        null,
        null,
        null
    ) ?: return ""
    val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
    returnCursor.moveToFirst()
    val fileName = returnCursor.getString(nameIndex)
    returnCursor.close()
    return fileName
}

fun Uri.getType(contentResolver: ContentResolver): String? {
    return contentResolver.getType(this)?.substringAfterLast("/")
}

fun Uri.isImage(contentResolver: ContentResolver): Boolean {
    return this.getType(contentResolver)?.let {
        val imageTypes = arrayOf("jpeg", "jpg", "png", "bmp", "gif", "webp")
        return imageTypes.contains(it.lowercase(Locale.ROOT))
    } ?: return false
}

fun Bitmap.toUri(context: Context): Uri? {
    val contentResolver = context.contentResolver
    val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "image_${System.currentTimeMillis()}")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
    }

    var imageUri: Uri? = null

    try {
        imageUri = contentResolver.insert(uri, contentValues)
        imageUri?.let {
            contentResolver.openOutputStream(it).use { outputStream ->
                this.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }

    return imageUri
}
