package com.on.staccato.domain.repository

import com.on.staccato.domain.ApiResult
import com.on.staccato.domain.UploadFile

interface ImageRepository {
    suspend fun convertImageFileToUrl(imageFile: UploadFile): ApiResult<String>
}
