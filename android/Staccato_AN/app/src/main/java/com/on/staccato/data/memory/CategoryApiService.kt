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

interface CategoryApiService {
    @GET(CATEGORY_PATH_WITH_ID)
    suspend fun getCategory(
        @Path(CATEGORY_ID) categoryId: Long,
    ): Response<MemoryResponse>

    @GET(CATEGORY_PATH_WITH_CANDIDATES)
    suspend fun getCategories(
        @Query(CURRENT_DATE) currentDate: String?,
    ): Response<MemoriesResponse>

    @POST(CATEGORIES_PATH)
    suspend fun postCategory(
        @Body categoryRequest: MemoryRequest,
    ): Response<MemoryCreationResponse>

    @PUT(CATEGORY_PATH_WITH_ID)
    suspend fun putCategory(
        @Path(CATEGORY_ID) categoryId: Long,
        @Body categoryRequest: MemoryRequest,
    ): Response<Unit>

    @DELETE(CATEGORY_PATH_WITH_ID)
    suspend fun deleteCategory(
        @Path(CATEGORY_ID) categoryId: Long,
    ): Response<Unit>

    companion object {
        const val CATEGORIES_PATH = "/categories"
        private const val CANDIDATES_PATH = "/candidates"
        private const val CATEGORY_ID = "categoryId"
        private const val CATEGORY_PATH_WITH_ID = "$CATEGORIES_PATH/{$CATEGORY_ID}"
        private const val CATEGORY_PATH_WITH_CANDIDATES = "$CATEGORIES_PATH$CANDIDATES_PATH"
        private const val CURRENT_DATE = "currentDate"
    }
}
