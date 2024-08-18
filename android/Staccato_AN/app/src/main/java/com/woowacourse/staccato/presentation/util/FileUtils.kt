package com.woowacourse.staccato.presentation.util

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

private const val MEMORY_FILE_CHILD_NAME = "memory"

fun convertMemoryUriToFile(
    context: Context,
    uri: Uri,
    name: String,
): MultipartBody.Part {
    val contextResolver: ContentResolver = context.contentResolver
    // 파일 이름과 MIME 타입 가져오기
    val contentType = contextResolver.getType(uri)?.toMediaTypeOrNull()
    // Uri로부터 InputStream을 얻고, 임시 파일로 복사
    val inputStream = contextResolver.openInputStream(uri)

    val file = File(context.cacheDir, MEMORY_FILE_CHILD_NAME)
    inputStream.use { input ->
        file.outputStream().use { output ->
            input?.copyTo(output)
        }
    }

    val requestFile = file.asRequestBody(contentType)
    return MultipartBody.Part.createFormData(name, file.name, requestFile)
}

fun convertExcretaFile(
    context: Context,
    uri: Uri,
    name: String,
): MultipartBody.Part {
    if (uri.toString().isEmpty()) {
        val emptyFile = "".toRequestBody("multipart/form-data".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(name, "", emptyFile)
    }

    // 파일 이름과 MIME 타입 가져오기
    val contentType = context.contentResolver.getType(uri)?.toMediaTypeOrNull()

    // Uri로부터 InputStream을 얻고, 임시 파일로 복사
    val inputStream = context.contentResolver.openInputStream(uri)
    val file = File(context.cacheDir, "tempFile_${System.currentTimeMillis()}")
    inputStream.use { input ->
        file.outputStream().use { output ->
            input?.copyTo(output)
        }
    }

    val requestFile = file.asRequestBody(contentType)
    return MultipartBody.Part.createFormData(name, file.name, requestFile)
}
