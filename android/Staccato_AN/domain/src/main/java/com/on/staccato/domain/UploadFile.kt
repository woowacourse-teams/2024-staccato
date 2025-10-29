package com.on.staccato.domain

import java.io.File

data class UploadFile(
    val file: File,
    val contentType: String?,
)
