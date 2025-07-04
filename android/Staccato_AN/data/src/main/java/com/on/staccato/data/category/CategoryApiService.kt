package com.on.staccato.data.category

import com.on.staccato.data.dto.category.CategoriesResponse
import com.on.staccato.data.dto.category.CategoryColorRequest
import com.on.staccato.data.dto.category.CategoryCreationResponse
import com.on.staccato.data.dto.category.CategoryRequest
import com.on.staccato.data.dto.category.CategoryResponse
import com.on.staccato.data.dto.timeline.TimelineResponse
import com.on.staccato.domain.ApiResult
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface CategoryApiService {
    @GET(CATEGORY_PATH_WITH_ID_V3)
    suspend fun getCategory(
        @Path(CATEGORY_ID) categoryId: Long,
    ): ApiResult<CategoryResponse>

    @PUT(CATEGORY_COLOR_CHANGE_PATH)
    suspend fun putCategoryColor(
        @Path(CATEGORY_ID) categoryId: Long,
        @Body color: CategoryColorRequest,
    ): ApiResult<Unit>

    @GET(CATEGORIES_PATH_V3)
    suspend fun getCategories(
        @Query(SORT) sort: String? = null,
        @Query(FILTERS) filter: String? = null,
    ): ApiResult<TimelineResponse>

    // TODO: 현재 사용 되지 않음
    @GET(CATEGORY_PATH_WITH_CANDIDATES)
    suspend fun getCategoriesBy(
        @Query(CURRENT_DATE) currentDate: String?,
    ): ApiResult<CategoriesResponse>

    @POST(CATEGORIES_PATH_V3)
    suspend fun postCategory(
        @Body categoryRequest: CategoryRequest,
    ): ApiResult<CategoryCreationResponse>

    @PUT("$CATEGORIES_PATH_V2/{$CATEGORY_ID}")
    suspend fun putCategory(
        @Path(CATEGORY_ID) categoryId: Long,
        @Body categoryRequest: CategoryRequest,
    ): ApiResult<Unit>

    @DELETE(CATEGORY_PATH_WITH_ID)
    suspend fun deleteCategory(
        @Path(CATEGORY_ID) categoryId: Long,
    ): ApiResult<Unit>

    @DELETE(CATEGORY_MEMBERS_ME_PATH)
    suspend fun deleteMeFromCategory(
        @Path(CATEGORY_ID) categoryId: Long,
    ): ApiResult<Unit>

    companion object {
        private const val CATEGORIES_PATH = "/categories"
        private const val CANDIDATES_PATH = "/candidates"
        private const val CATEGORY_ID = "categoryId"
        private const val CATEGORY_PATH_WITH_ID = "$CATEGORIES_PATH/{$CATEGORY_ID}"
        private const val CATEGORY_COLOR_CHANGE_PATH = "$CATEGORIES_PATH/{$CATEGORY_ID}/colors"
        private const val CATEGORY_PATH_WITH_CANDIDATES = "$CATEGORIES_PATH$CANDIDATES_PATH"
        private const val CATEGORIES_PATH_V2 = "/v2${CATEGORIES_PATH}"
        private const val CATEGORY_PATH_WITH_ID_V3 = "/v3$CATEGORIES_PATH/{$CATEGORY_ID}"
        private const val CATEGORIES_PATH_V3 = "/v3${CATEGORIES_PATH}"
        private const val CATEGORY_MEMBERS_PATH = "$CATEGORY_PATH_WITH_ID/members"
        private const val CATEGORY_MEMBERS_ME_PATH = "$CATEGORY_MEMBERS_PATH/me"
        private const val SORT = "sort"
        private const val FILTERS = "filters"
        private const val CURRENT_DATE = "currentDate"
    }
}
