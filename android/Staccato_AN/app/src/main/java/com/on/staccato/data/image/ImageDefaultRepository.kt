package com.on.staccato.data.image

import com.on.staccato.data.ApiResponseHandler.handleApiResponse
import com.on.staccato.data.ResponseResult
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
                is ResponseResult.Exception -> ResponseResult.Exception(responseResult.e, ERROR_MESSAGE)
                is ResponseResult.ServerError ->
                    ResponseResult.ServerError(
                        responseResult.status,
                        responseResult.message,
                    )

                is ResponseResult.Success -> ResponseResult.Success(responseResult.data)
            }
        }

        companion object {
            const val ERROR_MESSAGE = "이미지 업로드에 실패했습니다."
        }
    }
