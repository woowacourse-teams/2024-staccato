package com.on.staccato.data

import com.on.staccato.data.dto.Status

private const val EXCEPTION_NETWORK_ERROR_MESSAGE = "네트워크 연결이 불안정합니다.\n연결을 재설정한 후 다시 시도해 주세요."

sealed interface ApiResult<T : Any>

class Success<T : Any>(val data: T) : ApiResult<T>

class ServerError<T : Any>(val status: Status, val message: String) : ApiResult<T>

class Exception<T : Any>(val e: Throwable, val message: String = EXCEPTION_NETWORK_ERROR_MESSAGE) : ApiResult<T>

inline fun <T : Any, R : Any> ApiResult<T>.handle(convert: (T) -> R): ApiResult<R> =
    when (this) {
        is Exception -> Exception(e)
        is ServerError -> ServerError(status, message)
        is Success -> Success(convert(data))
    }
