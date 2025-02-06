package com.on.staccato.data

import com.on.staccato.data.dto.Status

sealed interface ApiResult<T>

class Success<T>(val data: T) : ApiResult<T>

class ServerError<T>(val status: Status, val message: String) : ApiResult<T>

sealed class Exception<T> : ApiResult<T> {
    class NetworkError<T> : Exception<T>()

    class UnknownError<T> : Exception<T>()
}

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
