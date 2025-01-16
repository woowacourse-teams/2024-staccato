package com.on.staccato.data

import retrofit2.http.GET
import retrofit2.http.Path

interface FakeApiService {
    @GET("/get/{id}")
    suspend fun get(
        @Path("id") id: Long,
    ): ApiResult<GetResponse>
}
