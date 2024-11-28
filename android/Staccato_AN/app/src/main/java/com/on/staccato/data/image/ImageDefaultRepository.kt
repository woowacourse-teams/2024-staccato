package com.on.staccato.data.image

import com.on.staccato.data.ApiResponseHandler.handleApiResponse
import com.on.staccato.data.Exception
import com.on.staccato.data.ResponseResult
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
        override suspend fun convertImageFileToUrl(imageFile: MultipartBody.Part): ResponseResult<ImageResponse> {
            val responseResult = handleApiResponse { imageApiService.postImage(imageFile) }
            return when (responseResult) {
                is Exception -> Exception(responseResult.e, EXCEPTION_NETWORK_ERROR_MESSAGE)
                is ServerError -> ServerError(responseResult.status, responseResult.message)
                is Success -> Success(responseResult.data)
            }
        }

        companion object {
            const val EXCEPTION_NETWORK_ERROR_MESSAGE = "네트워크 연결이 불안정합니다.\n연결을 재설정한 후 다시 시도해 주세요."
        }
    }
