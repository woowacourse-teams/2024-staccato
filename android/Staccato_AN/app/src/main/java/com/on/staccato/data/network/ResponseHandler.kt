package com.on.staccato.data.network

import com.on.staccato.data.dto.ErrorResponse
import com.on.staccato.data.dto.Status
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

class ResponseHandler {
    @Inject
    lateinit var errorConverter: ErrorConverter

    fun <T : Any> handleApiResponse(execute: () -> Response<T>): ApiResult<T> {
        return try {
            val response: Response<T> = execute()
            val body: T? = response.body()

            when {
                response.isSuccessful && response.code() == CREATED -> Success(body as T)
                response.isSuccessful && body != null -> Success(body)
                else -> {
                    val errorBody: ResponseBody =
                        response.errorBody()
                            ?: throw IllegalArgumentException(NOT_FOUND_ERROR_BODY)
                    val errorResponse: ErrorResponse = errorConverter.convert(errorBody)
                    ServerError(
                        status = Status.Message(errorResponse.status),
                        message = errorResponse.message,
                    )
                }
            }
        } catch (httpException: HttpException) {
            ServerError(status = Status.Code(httpException.code()), message = httpException.message())
        } catch (throwable: Throwable) {
            handleException<T>(throwable)
        }
    }

    fun <T : Any> handleException(throwable: Throwable) =
        when (throwable) {
            is IOException -> Exception.NetworkError<T>()
            else -> Exception.UnknownError<T>()
        }

    companion object {
        private const val CREATED = 201
        private const val NOT_FOUND_ERROR_BODY = "errorBody를 찾을 수 없습니다."
    }
}
