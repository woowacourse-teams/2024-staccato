package com.on.staccato.data.image

import com.on.staccato.data.dto.image.ImageResponse
import com.on.staccato.domain.ApiResult
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ImageApiService {
    @Multipart
    @POST(IMAGE_PATH)
    suspend fun postImage(
        @Part imageFile: MultipartBody.Part,
    ): ApiResult<ImageResponse>

    companion object {
        const val IMAGE_PATH = "/images"
    }
}
