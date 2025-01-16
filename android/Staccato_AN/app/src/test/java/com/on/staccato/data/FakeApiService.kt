package com.on.staccato.data

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
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
}
