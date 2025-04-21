package com.on.staccato.data.comment

import com.on.staccato.data.dto.comment.CommentRequest
import com.on.staccato.data.dto.comment.CommentUpdateRequest
import com.on.staccato.data.dto.comment.CommentsResponse
import com.on.staccato.data.network.ApiResult

interface CommentDataSource {
    suspend fun getComments(staccatoId: Long): ApiResult<CommentsResponse>

    suspend fun createComment(commentRequest: CommentRequest): ApiResult<Unit>

    suspend fun updateComment(
        commentId: Long,
        commentUpdateRequest: CommentUpdateRequest,
    ): ApiResult<Unit>

    suspend fun deleteComment(commentId: Long): ApiResult<Unit>
}
