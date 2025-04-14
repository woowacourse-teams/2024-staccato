package com.on.staccato.domain.repository

import com.on.staccato.data.dto.image.ImageResponse
import com.on.staccato.data.network.ApiResult
import okhttp3.MultipartBody

interface ImageRepository {
    suspend fun convertImageFileToUrl(imageFile: MultipartBody.Part): ApiResult<ImageResponse>
}
