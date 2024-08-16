package com.woowacourse.staccato.domain.repository

import com.woowacourse.staccato.data.ResponseResult
import com.woowacourse.staccato.data.dto.image.ImageResponse
import okhttp3.MultipartBody

interface ImageRepository {
    suspend fun convertImageFileToUrl(imageFile: MultipartBody.Part): ResponseResult<ImageResponse>
}
