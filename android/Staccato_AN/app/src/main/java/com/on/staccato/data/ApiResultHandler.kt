package com.on.staccato.data

import com.on.staccato.data.dto.Status
import com.on.staccato.presentation.util.ExceptionState

inline fun <T : Any> ApiResult<T>.onSuccess(executable: (T) -> Unit): ApiResult<T> =
    apply {
        if (this is Success<T>) executable(data)
    }

inline fun <T : Any> ApiResult<T>.onServerError(executable: (status: Status, message: String) -> Unit): ApiResult<T> =
    apply {
        if (this is ServerError<T>) executable(status, message)
    }

inline fun <T : Any> ApiResult<T>.onException(executable: (exceptionState: ExceptionState) -> Unit): ApiResult<T> =
    apply {
        if (this is Exception<T>) {
            when (this) {
                is Exception.NetworkError -> executable(ExceptionState.NetworkError)
                is Exception.UnknownError -> executable(ExceptionState.UnknownError)
            }
        }
    }
