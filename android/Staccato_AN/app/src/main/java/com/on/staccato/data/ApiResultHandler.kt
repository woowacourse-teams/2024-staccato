package com.on.staccato.data

import com.on.staccato.data.dto.Status
import com.on.staccato.presentation.util.ExceptionState

suspend fun <T : Any> ApiResult<T>.onSuccess(executable: suspend (T) -> Unit): ApiResult<T> =
    apply {
        if (this is Success<T>) executable(data)
    }

suspend fun <T : Any> ApiResult<T>.onServerError(executable: suspend (status: Status, message: String) -> Unit): ApiResult<T> =
    apply {
        if (this is ServerError<T>) executable(status, message)
    }

suspend fun <T : Any> ApiResult<T>.onException(executable: suspend (exceptionState: ExceptionState) -> Unit): ApiResult<T> =
    apply {
        if (this is Exception<T>) {
            when (this) {
                is Exception.NetworkError -> executable(ExceptionState.NetworkError)
                is Exception.UnknownError -> executable(ExceptionState.UnknownError)
            }
        }
    }
