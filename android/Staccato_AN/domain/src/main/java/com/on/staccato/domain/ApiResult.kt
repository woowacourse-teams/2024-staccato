package com.on.staccato.domain

sealed interface ApiResult<T>

class Success<T>(val data: T) : ApiResult<T>

class ServerError<T>(val status: Status, val message: String) : ApiResult<T>

sealed class Exception<T> : ApiResult<T> {
    class NetworkError<T> : Exception<T>()

    class UnknownError<T> : Exception<T>()
}
