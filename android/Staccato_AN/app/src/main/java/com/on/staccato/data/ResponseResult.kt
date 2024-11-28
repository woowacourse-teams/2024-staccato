package com.on.staccato.data

import com.on.staccato.data.dto.Status

private const val EXCEPTION_NETWORK_ERROR_MESSAGE = "네트워크 연결이 불안정합니다.\n연결을 재설정한 후 다시 시도해 주세요."

sealed interface ResponseResult<T : Any>

class Success<T : Any>(val data: T) : ResponseResult<T>

class ServerError<T : Any>(val status: Status, val message: String) : ResponseResult<T>

class Exception<T : Any>(val e: Throwable, val message: String = EXCEPTION_NETWORK_ERROR_MESSAGE) : ResponseResult<T>
