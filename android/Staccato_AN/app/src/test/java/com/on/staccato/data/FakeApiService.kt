package com.on.staccato.data

import com.on.staccato.data.dto.GetResponse
import com.on.staccato.data.dto.ImagePostResponse
import com.on.staccato.data.dto.PostRequest
import com.on.staccato.data.dto.PostResponse
import com.on.staccato.data.network.ApiResult
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface FakeApiService {
    @GET("/get/{id}")
    suspend fun get(
        @Path("id") id: Long,
    ): ApiResult<GetResponse>

    @POST("/post")
    suspend fun post(
        @Body request: PostRequest,
    ): ApiResult<PostResponse>

    @DELETE("/delete/{id}")
    suspend fun delete(
        @Path("id") id: Long,
    ): ApiResult<Unit>

    @Multipart
    @POST("/images")
    suspend fun postImage(
        @Part imageFile: MultipartBody.Part,
    ): ApiResult<ImagePostResponse>
}
