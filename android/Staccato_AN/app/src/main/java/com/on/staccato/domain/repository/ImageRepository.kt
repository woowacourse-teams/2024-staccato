package com.on.staccato.domain.repository

import com.on.staccato.data.ResponseResult
import com.on.staccato.data.dto.image.ImageResponse
import okhttp3.MultipartBody

interface ImageRepository {
    suspend fun convertImageFileToUrl(imageFile: MultipartBody.Part): ResponseResult<ImageResponse>
}
