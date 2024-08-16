package com.woowacourse.staccato.data

import com.woowacourse.staccato.data.dto.image.ImageResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ImageApiService {
    @Multipart
    @POST(IMAGE_PATH)
    suspend fun postImage(
        @Part imageFile: MultipartBody.Part,
    ): Response<ImageResponse>

    companion object {
        const val IMAGE_PATH = "/images"
    }
}
