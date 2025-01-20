package com.on.staccato.data

import com.on.staccato.data.dto.Status
import com.on.staccato.presentation.util.ExceptionState

inline fun <T : Any> ApiResult<T>.onSuccess(action: (T) -> Unit): ApiResult<T> =
    apply {
        if (this is Success<T>) action(data)
    }

inline fun <T : Any> ApiResult<T>.onServerError(action: (status: Status, message: String) -> Unit): ApiResult<T> =
    apply {
        if (this is ServerError<T>) action(status, message)
    }

inline fun <T : Any> ApiResult<T>.onException(action: (exceptionState: ExceptionState) -> Unit): ApiResult<T> =
    apply {
        if (this is Exception<T>) {
            when (this) {
                is Exception.NetworkError -> action(ExceptionState.NetworkError)
                is Exception.UnknownError -> action(ExceptionState.UnknownError)
            }
        }
    }
