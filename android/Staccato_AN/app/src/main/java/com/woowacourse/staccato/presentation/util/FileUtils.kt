package com.woowacourse.staccato.presentation.util

import android.content.Context
import android.net.Uri
import java.io.File

fun Context.getFileFromUri(selectedImageUri: Uri): File {
    val cursor = contentResolver.query(selectedImageUri, null, null, null, null)
    cursor!!.moveToNext()
    val dataIndex = cursor.getColumnIndex("_data").coerceAtLeast(0)
    val path = cursor.getString(dataIndex)
    cursor.close()
    return File(path)
}
