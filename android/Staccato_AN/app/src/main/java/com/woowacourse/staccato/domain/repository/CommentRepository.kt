package com.woowacourse.staccato.domain.repository

import com.woowacourse.staccato.data.ResponseResult
import com.woowacourse.staccato.domain.model.Comment
import com.woowacourse.staccato.domain.model.NewComment

interface CommentRepository {
    suspend fun fetchComments(momentId: Long): ResponseResult<List<Comment>>

    suspend fun createComment(newComment: NewComment): ResponseResult<Unit>

    suspend fun updateComment(
        commentId: Long,
        content: String,
    ): ResponseResult<Unit>

    suspend fun deleteComment(commentId: Long): ResponseResult<Unit>
}
