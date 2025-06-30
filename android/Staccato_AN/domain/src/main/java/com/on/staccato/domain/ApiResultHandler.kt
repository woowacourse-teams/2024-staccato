package com.on.staccato.domain

inline fun <T> ApiResult<T>.onSuccess(action: (T) -> Unit): ApiResult<T> =
    apply {
        if (this is Success<T>) action(data)
    }

inline fun <T> ApiResult<T>.onServerError(action: (message: String) -> Unit): ApiResult<T> =
    apply {
        if (this is ServerError<T>) action(message)
    }

inline fun <T> ApiResult<T>.onException(action: (exceptionState: ExceptionType) -> Unit): ApiResult<T> =
    apply {
        if (this is Exception<T>) {
            when (this) {
                is Exception.NetworkError -> action(ExceptionType.NETWORK)
                is Exception.UnknownError -> action(ExceptionType.UNKNOWN)
            }
        }
    }
