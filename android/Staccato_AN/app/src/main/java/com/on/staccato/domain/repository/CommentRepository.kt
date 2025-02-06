package com.on.staccato.domain.repository

import com.on.staccato.data.ApiResult
import com.on.staccato.domain.model.Comment
import com.on.staccato.domain.model.NewComment

interface CommentRepository {
    suspend fun fetchComments(staccatoId: Long): ApiResult<List<Comment>>

    suspend fun createComment(newComment: NewComment): ApiResult<Unit>

    suspend fun updateComment(
        commentId: Long,
        content: String,
    ): ApiResult<Unit>

    suspend fun deleteComment(commentId: Long): ApiResult<Unit>
}
