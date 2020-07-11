package com.jddeep.videocompressor.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.provider.MediaStore

class Utils {

    @SuppressLint("Recycle")
    fun getPath(uri: Uri?, context: Context): String {
        val projectionArray = arrayOf(MediaStore.Video.Media.DATA)

        if (uri == null) return ""
        val cursor = context.contentResolver.query(
            uri, projectionArray,
            null, null, null
        )

        return if (cursor != null) {
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(columnIndex)
        } else ""
    }
}