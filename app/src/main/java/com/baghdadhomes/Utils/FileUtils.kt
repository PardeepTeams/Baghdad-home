package com.baghdadhomes.Utils

import android.content.Context
import android.net.Uri
import android.provider.MediaStore

object FileUtils {
    fun getPath(context: Context, uri: Uri): String? {
        return when (uri.scheme) {
            "content" -> {
                val projection = arrayOf(MediaStore.Video.Media.DATA)
                context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                    val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
                    if (cursor.moveToFirst()) cursor.getString(columnIndex) else null
                }
            }
            "file" -> uri.path
            else -> null
        }
    }
}
