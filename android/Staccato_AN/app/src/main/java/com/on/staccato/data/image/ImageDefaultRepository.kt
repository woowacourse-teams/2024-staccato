package com.on.staccato.data.image

import com.on.staccato.data.ApiResult
import com.on.staccato.data.dto.image.ImageResponse
import com.on.staccato.data.handle
import com.on.staccato.domain.repository.ImageRepository
import okhttp3.MultipartBody
import javax.inject.Inject

class ImageDefaultRepository
    @Inject
    constructor(
        private val imageApiService: ImageApiService,
    ) : ImageRepository {
        override suspend fun convertImageFileToUrl(imageFile: MultipartBody.Part): ApiResult<ImageResponse> =
            imageApiService.postImage(imageFile).handle { it }
    }
