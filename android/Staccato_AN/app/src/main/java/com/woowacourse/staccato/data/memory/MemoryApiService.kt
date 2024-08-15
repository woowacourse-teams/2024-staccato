package com.woowacourse.staccato.data.memory

import com.woowacourse.staccato.data.dto.memory.MemoryCreationResponse
import com.woowacourse.staccato.data.dto.memory.MemoryRequest
import com.woowacourse.staccato.data.dto.memory.MemoryResponse
import com.woowacourse.staccato.data.dto.memory.MemoryUpdateRequest
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface MemoryApiService {
    @GET(MEMORY_PATH_WITH_ID)
    suspend fun getTravel(
        @Path("memoryId") memoryId: Long,
    ): Response<MemoryResponse>

    @Multipart
    @POST(MEMORIES_PATH)
    suspend fun postTravel(
        @Part("data") data: MemoryRequest,
        @Part thumbnailFile: MultipartBody.Part?,
    ): Response<MemoryCreationResponse>

    @Multipart
    @PUT(MEMORY_PATH_WITH_ID)
    suspend fun putTravel(
        @Path("memoryId") memoryId: Long,
        @Part("data") data: MemoryUpdateRequest,
        @Part memoryThumbnailFile: MultipartBody.Part?,
    ): Response<String>

    @DELETE(MEMORY_PATH_WITH_ID)
    suspend fun deleteTravel(
        @Path("memoryId") memoryId: Long,
    ): Response<Unit>

    companion object {
        private const val MEMORIES_PATH = "/memories"
        private const val MEMORY_ID = "/{memoryId}"
        private const val MEMORY_PATH_WITH_ID = "$MEMORIES_PATH$MEMORY_ID"
    }
}
