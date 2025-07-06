package com.on.staccato.presentation.util

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import com.on.staccato.domain.UploadFile
import java.io.File

const val DEFAULT_FILE_NAME = "name"

fun convertUriToFile(
    context: Context,
    uri: Uri,
    fileName: String = DEFAULT_FILE_NAME,
): UploadFile {
    val contextResolver: ContentResolver = context.contentResolver
    val contentType = contextResolver.getType(uri) // 파일 이름과 MIME 타입 가져오기
    val inputStream = contextResolver.openInputStream(uri) // Uri로부터 InputStream을 얻고, 임시 파일로 복사
    val file = File(context.cacheDir, fileName)
    inputStream.use { input ->
        file.outputStream().use { output ->
            input?.copyTo(output)
        }
    }
    return UploadFile(file, contentType)
}
