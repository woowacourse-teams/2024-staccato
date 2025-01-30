package com.on.staccato.presentation.util

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

private const val CATEGORY_FILE_CHILD_NAME = "category"
private const val STACCATO_FILE_CHILD_NAME = "staccato"

fun convertCategoryUriToFile(
    context: Context,
    uri: Uri,
    name: String,
): MultipartBody.Part {
    val contextResolver: ContentResolver = context.contentResolver
    // 파일 이름과 MIME 타입 가져오기
    val contentType = contextResolver.getType(uri)?.toMediaTypeOrNull()
    // Uri로부터 InputStream을 얻고, 임시 파일로 복사
    val inputStream = contextResolver.openInputStream(uri)

    val file = File(context.cacheDir, CATEGORY_FILE_CHILD_NAME)
    inputStream.use { input ->
        file.outputStream().use { output ->
            input?.copyTo(output)
        }
    }

    val requestFile = file.asRequestBody(contentType)
    return MultipartBody.Part.createFormData(name, file.name, requestFile)
}

fun convertStaccatoUriToFile(
    context: Context,
    uri: Uri?,
    name: String,
): MultipartBody.Part {
    if (uri.toString().isEmpty() || uri == null) {
        val emptyFile = "".toRequestBody("multipart/form-data".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(name, "", emptyFile)
    }
    // 컨텐츠 리소스의 접근을 도와주는 contentResolver
    val contentResolver: ContentResolver = context.contentResolver
    // uri로부터 MIME 타입 가져오기
    val contentType = contentResolver.getType(uri)?.toMediaTypeOrNull()
    // contentResolver로부터 uri에 대한 InputStream을 얻고,
    val inputStream = contentResolver.openInputStream(uri)
    // 임시 파일로 복사
    val file = File(context.cacheDir, "${STACCATO_FILE_CHILD_NAME}_${System.currentTimeMillis()}")
    inputStream.use { input ->
        file.outputStream().use { output ->
            input?.copyTo(output)
        }
    }
    // MultipartBody 생성하여 반환
    val requestFile = file.asRequestBody(contentType)
    return MultipartBody.Part.createFormData(name, file.name, requestFile)
}
