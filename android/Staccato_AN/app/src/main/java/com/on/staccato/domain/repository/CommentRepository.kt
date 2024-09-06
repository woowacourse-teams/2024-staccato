package com.on.staccato.domain.repository

import com.on.staccato.data.ResponseResult
import com.on.staccato.domain.model.Comment
import com.on.staccato.domain.model.NewComment

interface CommentRepository {
    suspend fun fetchComments(momentId: Long): ResponseResult<List<Comment>>

    suspend fun createComment(newComment: NewComment): ResponseResult<Unit>

    suspend fun updateComment(
        commentId: Long,
        content: String,
    ): ResponseResult<Unit>

    suspend fun deleteComment(commentId: Long): ResponseResult<Unit>
}
