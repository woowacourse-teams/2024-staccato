package com.on.staccato.data.network

import com.on.staccato.domain.ApiResult
import com.on.staccato.domain.Exception
import com.on.staccato.domain.ServerError
import com.on.staccato.domain.Success

inline fun <T, R> ApiResult<T>.handle(convert: (T) -> R): ApiResult<R> =
    when (this) {
        is Exception.NetworkError -> Exception.NetworkError()
        is Exception.UnknownError -> Exception.UnknownError()
        is ServerError -> ServerError(status, message)
        is Success -> Success(convert(data))
    }

fun ApiResult<Unit>.handle(): ApiResult<Unit> =
    when (this) {
        is Exception.NetworkError -> Exception.NetworkError()
        is Exception.UnknownError -> Exception.UnknownError()
        is ServerError -> ServerError(status, message)
        is Success -> Success(data)
    }
