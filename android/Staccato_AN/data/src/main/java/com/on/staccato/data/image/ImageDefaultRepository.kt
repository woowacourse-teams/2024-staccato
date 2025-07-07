package com.on.staccato.data.image

import com.on.staccato.data.network.handle
import com.on.staccato.domain.ApiResult
import com.on.staccato.domain.UploadFile
import com.on.staccato.domain.repository.ImageRepository
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import javax.inject.Inject

class ImageDefaultRepository
    @Inject
    constructor(
        private val imageApiService: ImageApiService,
    ) : ImageRepository {
        override suspend fun convertImageFileToUrl(imageFile: UploadFile): ApiResult<String> =
            imageApiService.postImage(createFormData(imageFile)).handle { it.imageUrl }

        private fun createFormData(uploadFile: UploadFile): MultipartBody.Part {
            val mediaType: MediaType? = uploadFile.contentType?.toMediaTypeOrNull()
            val requestFile: RequestBody = uploadFile.file.asRequestBody(mediaType)

            return MultipartBody.Part.createFormData(
                IMAGE_FORM_DATA_NAME,
                IMAGE_FILE_CHILD_NAME,
                requestFile,
            )
        }

        companion object {
            const val IMAGE_FORM_DATA_NAME = "imageFile"
            const val IMAGE_FILE_CHILD_NAME = "image"
        }
    }
