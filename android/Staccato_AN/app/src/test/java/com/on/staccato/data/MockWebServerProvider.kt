package com.on.staccato.data

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import retrofit2.Retrofit

fun buildRetrofitFor(mockWebServer: MockWebServer): Retrofit {
    val jsonBuilder = Json { coerceInputValues = true }
    val url = mockWebServer.url("/")

    return Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(
            jsonBuilder.asConverterFactory("application/json".toMediaType()),
        )
        .addCallAdapterFactory(ApiResultCallAdapterFactory.create())
        .build()
}

fun createMockResponse(
    code: Int,
    body: String,
) = MockResponse().setResponseCode(code).setBody(body)
