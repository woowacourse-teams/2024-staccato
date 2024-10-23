package com.on.staccato.data.comment

import com.on.staccato.data.ResponseResult
import com.on.staccato.data.dto.comment.CommentUpdateRequest
import com.on.staccato.data.dto.mapper.toDomain
import com.on.staccato.data.dto.mapper.toDto
import com.on.staccato.domain.model.Comment
import com.on.staccato.domain.model.NewComment
import com.on.staccato.domain.repository.CommentRepository
import javax.inject.Inject

class CommentDefaultRepository
    @Inject
    constructor(
        private val commentDataSource: CommentDataSource,
    ) : CommentRepository {
        override suspend fun fetchComments(staccatoId: Long): ResponseResult<List<Comment>> =
            when (val responseResult = commentDataSource.getComments(staccatoId)) {
                is ResponseResult.ServerError -> {
                    ResponseResult.ServerError(responseResult.status, responseResult.message)
                }

                is ResponseResult.Exception -> {
                    ResponseResult.Exception(responseResult.e, EXCEPTION_NETWORK_ERROR_MESSAGE)
                }

                is ResponseResult.Success -> {
                    ResponseResult.Success(responseResult.data.toDomain())
                }
            }

        override suspend fun createComment(newComment: NewComment): ResponseResult<Unit> =
            when (val responseResult = commentDataSource.createComment(newComment.toDto())) {
                is ResponseResult.ServerError -> {
                    ResponseResult.ServerError(responseResult.status, responseResult.message)
                }

                is ResponseResult.Exception -> {
                    ResponseResult.Exception(responseResult.e, EXCEPTION_NETWORK_ERROR_MESSAGE)
                }

                is ResponseResult.Success -> {
                    ResponseResult.Success(responseResult.data)
                }
            }

        override suspend fun updateComment(
            commentId: Long,
            content: String,
        ): ResponseResult<Unit> {
            val responseResult =
                commentDataSource.updateComment(
                    commentId,
                    CommentUpdateRequest(content),
                )
            return when (responseResult) {
                is ResponseResult.ServerError -> {
                    ResponseResult.ServerError(responseResult.status, responseResult.message)
                }

                is ResponseResult.Exception -> {
                    ResponseResult.Exception(responseResult.e, EXCEPTION_NETWORK_ERROR_MESSAGE)
                }

                is ResponseResult.Success -> {
                    ResponseResult.Success(responseResult.data)
                }
            }
        }

        override suspend fun deleteComment(commentId: Long): ResponseResult<Unit> =
            when (val responseResult = commentDataSource.deleteComment(commentId)) {
                is ResponseResult.ServerError -> {
                    ResponseResult.ServerError(responseResult.status, responseResult.message)
                }

                is ResponseResult.Exception -> {
                    ResponseResult.Exception(responseResult.e, EXCEPTION_NETWORK_ERROR_MESSAGE)
                }

                is ResponseResult.Success -> {
                    ResponseResult.Success(responseResult.data)
                }
            }

        companion object {
            private const val EXCEPTION_NETWORK_ERROR_MESSAGE = "네트워크 연결이 불안정합니다.\n연결을 재설정한 후 다시 시도해 주세요."
        }
    }
