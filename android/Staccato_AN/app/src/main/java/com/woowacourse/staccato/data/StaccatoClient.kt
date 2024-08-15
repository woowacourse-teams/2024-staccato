package com.woowacourse.staccato.data

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.woowacourse.staccato.BuildConfig
import com.woowacourse.staccato.data.dto.ErrorResponse
import com.woowacourse.staccato.data.login.LoginApiService
import com.woowacourse.staccato.data.memory.TravelApiService
import com.woowacourse.staccato.data.timeline.TimeLineApiService
import com.woowacourse.staccato.data.visit.VisitApiService
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

object StaccatoClient {
    private val BASE_URL = BuildConfig.BASE_URL
    private val tokenManager = TokenManager()

    val loginApiService: LoginApiService by lazy {
        create(LoginApiService::class.java)
    }

    val travelApiService: TravelApiService by lazy {
        create(TravelApiService::class.java)
    }

    val timelineService: TimeLineApiService by lazy {
        create(TimeLineApiService::class.java)
    }

    val visitApiService: VisitApiService by lazy {
        create(VisitApiService::class.java)
    }

    private val provideLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    private val provideHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(provideLoggingInterceptor)
            .addInterceptor(HeaderInterceptor(tokenManager))
            .build()

    private val jsonBuilder = Json { coerceInputValues = true }

    private val provideRetrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(provideHttpClient)
            .addConverterFactory(
                jsonBuilder.asConverterFactory("application/json".toMediaType()),
            )
            .build()

    fun getErrorResponse(errorBody: ResponseBody): ErrorResponse {
        return provideRetrofit.responseBodyConverter<ErrorResponse>(
            ErrorResponse::class.java,
            ErrorResponse::class.java.annotations,
        ).convert(errorBody) ?: throw IllegalArgumentException("errorBody를 변환할 수 없습니다.")
    }

    private fun <T> create(service: Class<T>): T {
        return provideRetrofit.create(service)
    }
}
