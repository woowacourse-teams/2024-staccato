package com.on.staccato.data

import com.on.staccato.data.StaccatoClient.getErrorResponse
import com.on.staccato.data.dto.ErrorResponse
import com.on.staccato.data.dto.Status
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response

object ApiResponseHandler {
    suspend fun <T : Any> handleApiResponse(execute: suspend () -> Response<T>): ResponseResult<T> {
        return try {
            val response: Response<T> = execute()
            val body: T? = response.body()

            when {
                response.isSuccessful && response.code() == 201 -> Success(body as T)
                response.isSuccessful && body != null -> Success(body)
                response.isSuccessful && response.code() == 204 -> Success(Unit as T)
                else -> {
                    val errorBody: ResponseBody =
                        response.errorBody()
                            ?: throw IllegalArgumentException("errorBody를 찾을 수 없습니다.")
                    val errorResponse: ErrorResponse = getErrorResponse(errorBody)
                    ServerError(
                        status = Status.Message(errorResponse.status),
                        message = errorResponse.message,
                    )
                }
            }
        } catch (e: HttpException) {
            ServerError(status = Status.Code(e.code()), message = e.message())
        } catch (e: Throwable) {
            Exception(e, message = e.message.toString())
        }
    }

    suspend fun <T : Any> ResponseResult<T>.onSuccess(executable: suspend (T) -> Unit): ResponseResult<T> =
        apply {
            if (this is Success<T>) {
                executable(data)
            }
        }

    suspend fun <T : Any> ResponseResult<T>.onServerError(
        executable: suspend (status: Status, message: String) -> Unit,
    ): ResponseResult<T> =
        apply {
            if (this is ServerError<T>) {
                executable(status, message)
            }
        }

    suspend fun <T : Any> ResponseResult<T>.onException(executable: suspend (e: Throwable, message: String) -> Unit): ResponseResult<T> =
        apply {
            if (this is Exception<T>) {
                executable(e, message)
            }
        }
}
