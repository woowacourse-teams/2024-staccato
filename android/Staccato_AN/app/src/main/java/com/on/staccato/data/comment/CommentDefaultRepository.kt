package com.on.staccato.data.comment

import com.on.staccato.data.Exception
import com.on.staccato.data.ResponseResult
import com.on.staccato.data.ServerError
import com.on.staccato.data.Success
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
                is ServerError -> ServerError(responseResult.status, responseResult.message)
                is Exception -> Exception(responseResult.e)
                is Success -> Success(responseResult.data.toDomain())
            }

        override suspend fun createComment(newComment: NewComment): ResponseResult<Unit> =
            when (val responseResult = commentDataSource.createComment(newComment.toDto())) {
                is ServerError -> ServerError(responseResult.status, responseResult.message)
                is Exception -> Exception(responseResult.e)
                is Success -> Success(responseResult.data)
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
                is ServerError -> ServerError(responseResult.status, responseResult.message)
                is Exception -> Exception(responseResult.e)
                is Success -> Success(responseResult.data)
            }
        }

        override suspend fun deleteComment(commentId: Long): ResponseResult<Unit> =
            when (val responseResult = commentDataSource.deleteComment(commentId)) {
                is ServerError -> ServerError(responseResult.status, responseResult.message)
                is Exception -> Exception(responseResult.e)
                is Success -> Success(responseResult.data)
            }
    }
