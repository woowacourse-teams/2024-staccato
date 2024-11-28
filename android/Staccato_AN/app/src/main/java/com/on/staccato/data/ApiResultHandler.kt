package com.on.staccato.data

import com.on.staccato.data.dto.Status

suspend fun <T : Any> ApiResult<T>.onSuccess(executable: suspend (T) -> Unit): ApiResult<T> =
    apply {
        if (this is Success<T>) {
            executable(data)
        }
    }

suspend fun <T : Any> ApiResult<T>.onServerError(executable: suspend (status: Status, message: String) -> Unit): ApiResult<T> =
    apply {
        if (this is ServerError<T>) {
            executable(status, message)
        }
    }

suspend fun <T : Any> ApiResult<T>.onException(executable: suspend (e: Throwable, message: String) -> Unit): ApiResult<T> =
    apply {
        if (this is Exception<T>) {
            executable(e, message)
        }
    }
