package com.on.staccato.data

import com.on.staccato.data.dto.Status

sealed interface ApiResult<T : Any>

class Success<T : Any>(val data: T) : ApiResult<T>

class ServerError<T : Any>(val status: Status, val message: String) : ApiResult<T>

sealed class Exception<T : Any> : ApiResult<T> {
    class NetworkError<T : Any> : Exception<T>()

    class UnknownError<T : Any> : Exception<T>()
}

inline fun <T : Any, R : Any> ApiResult<T>.handle(convert: (T) -> R): ApiResult<R> =
    when (this) {
        is Exception.NetworkError -> Exception.NetworkError()
        is Exception.UnknownError -> Exception.UnknownError()
        is ServerError -> ServerError(status, message)
        is Success -> Success(convert(data))
    }
