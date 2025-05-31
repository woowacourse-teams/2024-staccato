package com.on.staccato.data.network

import com.on.staccato.data.dto.ErrorResponse
import okhttp3.ResponseBody

fun interface ErrorConverter {
    fun convert(responseBody: ResponseBody): ErrorResponse
}
