package com.on.staccato.data.network

import com.on.staccato.data.dto.ErrorResponse
import kotlinx.serialization.json.Json
import okhttp3.ResponseBody
import javax.inject.Inject

class ErrorConverter
    @Inject
    constructor(
        private val json: Json,
    ) {
        fun convert(errorBody: ResponseBody): ErrorResponse =
            runCatching {
                json.decodeFromString<ErrorResponse>(errorBody.string())
            }.getOrElse { throwable ->
                throw IllegalArgumentException(EXCEPTION_CONVERT_ERROR_RESPONSE, throwable)
            }

        companion object {
            private const val EXCEPTION_CONVERT_ERROR_RESPONSE = "errorBody를 변환할 수 없습니다."
        }
    }
