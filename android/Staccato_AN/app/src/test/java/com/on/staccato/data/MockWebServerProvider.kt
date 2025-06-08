package com.on.staccato.data

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.on.staccato.data.network.ApiResultCallAdapterFactory
import com.on.staccato.data.network.ErrorConverter
import com.on.staccato.data.network.ResponseHandler
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import retrofit2.Retrofit

fun buildRetrofitFor(mockWebServer: MockWebServer): Retrofit {
    val json = Json { coerceInputValues = true }
    val url = mockWebServer.url("/")

    val errorConverter = ErrorConverter(json)
    val responseHandler = ResponseHandler(errorConverter)

    return Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(
            json.asConverterFactory("application/json".toMediaType()),
        )
        .addCallAdapterFactory(ApiResultCallAdapterFactory(responseHandler))
        .build()
}

fun createMockResponse(
    code: Int,
    body: String,
) = MockResponse().setResponseCode(code).setBody(body)
