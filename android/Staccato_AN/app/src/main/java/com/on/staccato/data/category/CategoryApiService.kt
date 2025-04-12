package com.on.staccato.data.category

import com.on.staccato.data.ApiResult
import com.on.staccato.data.dto.category.CategoriesResponse
import com.on.staccato.data.dto.category.CategoryColorRequest
import com.on.staccato.data.dto.category.CategoryCreationResponse
import com.on.staccato.data.dto.category.CategoryRequest
import com.on.staccato.data.dto.category.CategoryResponse
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
    ): ApiResult<CategoryResponse>

    @PUT(CATEGORY_COLOR_CHANGE_PATH)
    suspend fun putCategoryColor(
        @Path(CATEGORY_ID) categoryId: Long,
        @Body color: CategoryColorRequest,
    ): ApiResult<Unit>

    @GET(CATEGORY_PATH_WITH_CANDIDATES)
    suspend fun getCategories(
        @Query(CURRENT_DATE) currentDate: String?,
    ): ApiResult<CategoriesResponse>

    @POST(CATEGORIES_PATH)
    suspend fun postCategory(
        @Body categoryRequest: CategoryRequest,
    ): ApiResult<CategoryCreationResponse>

    @PUT(CATEGORY_PATH_WITH_ID)
    suspend fun putCategory(
        @Path(CATEGORY_ID) categoryId: Long,
        @Body categoryRequest: CategoryRequest,
    ): ApiResult<Unit>

    @DELETE(CATEGORY_PATH_WITH_ID)
    suspend fun deleteCategory(
        @Path(CATEGORY_ID) categoryId: Long,
    ): ApiResult<Unit>

    companion object {
        const val CATEGORIES_PATH = "/categories"
        private const val CANDIDATES_PATH = "/candidates"
        private const val CATEGORY_ID = "categoryId"
        private const val CATEGORY_PATH_WITH_ID = "$CATEGORIES_PATH/{$CATEGORY_ID}"
        private const val CATEGORY_COLOR_CHANGE_PATH = "$CATEGORIES_PATH/{$CATEGORY_ID}/colors"
        private const val CATEGORY_PATH_WITH_CANDIDATES = "$CATEGORIES_PATH$CANDIDATES_PATH"
        private const val CURRENT_DATE = "currentDate"
    }
}
