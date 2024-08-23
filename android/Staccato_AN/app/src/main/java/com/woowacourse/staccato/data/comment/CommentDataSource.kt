package com.woowacourse.staccato.data.comment

import com.woowacourse.staccato.data.ResponseResult
import com.woowacourse.staccato.data.dto.comment.CommentRequest
import com.woowacourse.staccato.data.dto.comment.CommentUpdateRequest
import com.woowacourse.staccato.data.dto.comment.CommentsResponse

interface CommentDataSource {
    suspend fun getComments(momentId: Long): ResponseResult<CommentsResponse>

    suspend fun createComment(commentRequest: CommentRequest): ResponseResult<Unit>

    suspend fun updateComment(
        commentId: Long,
        commentUpdateRequest: CommentUpdateRequest,
    ): ResponseResult<Unit>

    suspend fun deleteComment(commentId: Long): ResponseResult<Unit>
}
