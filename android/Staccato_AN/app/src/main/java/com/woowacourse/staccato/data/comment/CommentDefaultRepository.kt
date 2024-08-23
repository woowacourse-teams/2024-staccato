package com.woowacourse.staccato.data.comment

import com.woowacourse.staccato.data.ResponseResult
import com.woowacourse.staccato.data.dto.comment.CommentUpdateRequest
import com.woowacourse.staccato.data.dto.mapper.toDomain
import com.woowacourse.staccato.data.dto.mapper.toDto
import com.woowacourse.staccato.domain.model.Comment
import com.woowacourse.staccato.domain.model.NewComment
import com.woowacourse.staccato.domain.repository.CommentRepository

class CommentDefaultRepository(
    private val commentDataSource: CommentDataSource = CommentRemoteDataSource()
) : CommentRepository {
    override suspend fun fetchComments(momentId: Long): ResponseResult<List<Comment>> =
        when (val responseResult = commentDataSource.getComments(momentId)) {
            is ResponseResult.ServerError -> {
                ResponseResult.ServerError(responseResult.status, responseResult.message)
            }

            is ResponseResult.Exception -> {
                ResponseResult.Exception(responseResult.e, responseResult.message)
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
                ResponseResult.Exception(responseResult.e, responseResult.message)
            }

            is ResponseResult.Success -> {
                ResponseResult.Success(responseResult.data)
            }
        }

    override suspend fun updateComment(commentId: Long, content: String): ResponseResult<Unit> {
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
                ResponseResult.Exception(responseResult.e, responseResult.message)
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
                ResponseResult.Exception(responseResult.e, responseResult.message)
            }

            is ResponseResult.Success -> {
                ResponseResult.Success(responseResult.data)
            }
        }
}
