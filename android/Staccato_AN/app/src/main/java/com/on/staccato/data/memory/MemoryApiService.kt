package com.on.staccato.data.memory

import com.on.staccato.data.dto.memory.MemoriesResponse
import com.on.staccato.data.dto.memory.MemoryCreationResponse
import com.on.staccato.data.dto.memory.MemoryRequest
import com.on.staccato.data.dto.memory.MemoryResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface MemoryApiService {
    @GET(MEMORY_PATH_WITH_ID)
    suspend fun getMemory(
        @Path("memoryId") memoryId: Long,
    ): Response<MemoryResponse>

    @GET(MEMORY_PATH_WITH_CANDIDATES)
    suspend fun getMemories(
        @Query("currentDate") currentDate: String?,
    ): Response<MemoriesResponse>

    @POST(MEMORIES_PATH)
    suspend fun postMemory(
        @Body memoryRequest: MemoryRequest,
    ): Response<MemoryCreationResponse>

    @PUT(MEMORY_PATH_WITH_ID)
    suspend fun putMemory(
        @Path("memoryId") memoryId: Long,
        @Body memoryRequest: MemoryRequest,
    ): Response<Unit>

    @DELETE(MEMORY_PATH_WITH_ID)
    suspend fun deleteMemory(
        @Path("memoryId") memoryId: Long,
    ): Response<Unit>

    companion object {
        const val MEMORIES_PATH = "/memories"
        private const val CANDIDATES_PATH = "/candidates"
        private const val MEMORY_ID = "/{memoryId}"
        private const val MEMORY_PATH_WITH_ID = "$MEMORIES_PATH$MEMORY_ID"
        private const val MEMORY_PATH_WITH_CANDIDATES = "$MEMORIES_PATH$CANDIDATES_PATH"
    }
}
