package com.woowacourse.staccato.data

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.woowacourse.staccato.BuildConfig
import com.woowacourse.staccato.data.apiservice.TimeLineApiService
import com.woowacourse.staccato.data.travel.TravelApiService
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

object StaccatoClient {
    private val BASE_URL = BuildConfig.BASE_URL

    val travelApiService: TravelApiService by lazy {
        create(
            TravelApiService::class.java,
        )
    }

    val timelineService: TimeLineApiService by lazy {
        create(TimeLineApiService::class.java)
    }

    private val provideLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    private val provideHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(provideLoggingInterceptor)
            .addInterceptor(HeaderInterceptor())
            .build()

    private val jsonBuilder = Json { coerceInputValues = true }
    private val provideRetrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(provideHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(
                jsonBuilder.asConverterFactory("application/json".toMediaType()),
            )
            .build()

    private fun <T> create(service: Class<T>): T {
        return provideRetrofit.create(service)
    }
}
