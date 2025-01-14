package com.on.staccato.data.category

import com.on.staccato.data.dto.category.CategoriesResponse
import com.on.staccato.data.dto.category.CategoryCreationResponse
import com.on.staccato.data.dto.category.CategoryRequest
import com.on.staccato.data.dto.category.CategoryResponse
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
    ): Response<CategoryResponse>

    @GET(CATEGORY_PATH_WITH_CANDIDATES)
    suspend fun getCategories(
        @Query(CURRENT_DATE) currentDate: String?,
    ): Response<CategoriesResponse>

    @POST(CATEGORIES_PATH)
    suspend fun postCategory(
        @Body categoryRequest: CategoryRequest,
    ): Response<CategoryCreationResponse>

    @PUT(CATEGORY_PATH_WITH_ID)
    suspend fun putCategory(
        @Path(CATEGORY_ID) categoryId: Long,
        @Body categoryRequest: CategoryRequest,
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
