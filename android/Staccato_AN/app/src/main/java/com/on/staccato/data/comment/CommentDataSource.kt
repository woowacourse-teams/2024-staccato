package com.on.staccato.data.comment

import com.on.staccato.data.ResponseResult
import com.on.staccato.data.dto.comment.CommentRequest
import com.on.staccato.data.dto.comment.CommentUpdateRequest
import com.on.staccato.data.dto.comment.CommentsResponse

interface CommentDataSource {
    suspend fun getComments(momentId: Long): ResponseResult<CommentsResponse>

    suspend fun createComment(commentRequest: CommentRequest): ResponseResult<Unit>

    suspend fun updateComment(
        commentId: Long,
        commentUpdateRequest: CommentUpdateRequest,
    ): ResponseResult<Unit>

    suspend fun deleteComment(commentId: Long): ResponseResult<Unit>
}
