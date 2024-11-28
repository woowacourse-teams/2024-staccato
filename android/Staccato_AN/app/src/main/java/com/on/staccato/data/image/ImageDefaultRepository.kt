package com.on.staccato.data.image

import com.on.staccato.data.ApiResult
import com.on.staccato.data.Exception
import com.on.staccato.data.ServerError
import com.on.staccato.data.Success
import com.on.staccato.data.dto.image.ImageResponse
import com.on.staccato.domain.repository.ImageRepository
import okhttp3.MultipartBody
import javax.inject.Inject

class ImageDefaultRepository
    @Inject
    constructor(
        private val imageApiService: ImageApiService,
    ) : ImageRepository {
        override suspend fun convertImageFileToUrl(imageFile: MultipartBody.Part): ApiResult<ImageResponse> {
            return when (val responseResult = imageApiService.postImage(imageFile)) {
                is Exception -> Exception(responseResult.e)
                is ServerError -> ServerError(responseResult.status, responseResult.message)
                is Success -> Success(responseResult.data)
            }
        }
    }
