package com.on.staccato.data

import com.on.staccato.presentation.util.ExceptionState
import com.on.staccato.presentation.util.ExceptionState2

inline fun <T> ApiResult<T>.onSuccess(action: (T) -> Unit): ApiResult<T> =
    apply {
        if (this is Success<T>) action(data)
    }

inline fun <T> ApiResult<T>.onServerError(action: (message: String) -> Unit): ApiResult<T> =
    apply {
        if (this is ServerError<T>) action(message)
    }

inline fun <T> ApiResult<T>.onException(action: (exceptionState: ExceptionState) -> Unit): ApiResult<T> =
    apply {
        if (this is Exception<T>) {
            when (this) {
                is Exception.NetworkError -> action(ExceptionState.NetworkError)
                is Exception.UnknownError -> action(ExceptionState.UnknownError)
            }
        }
    }

inline fun <T> ApiResult<T>.onException2(action: (exceptionState: ExceptionState2) -> Unit): ApiResult<T> =
    apply {
        if (this is Exception<T>) {
            when (this) {
                is Exception.NetworkError -> action(ExceptionState2.NetworkError)
                is Exception.UnknownError -> action(ExceptionState2.UnknownError)
            }
        }
    }
