package com.on.staccato.data.comment

import com.on.staccato.data.ApiResult
import com.on.staccato.data.dto.comment.CommentUpdateRequest
import com.on.staccato.data.dto.mapper.toDomain
import com.on.staccato.data.dto.mapper.toDto
import com.on.staccato.data.handle
import com.on.staccato.domain.model.Comment
import com.on.staccato.domain.model.NewComment
import com.on.staccato.domain.repository.CommentRepository
import kotlinx.serialization.json.JsonNull.content
import javax.inject.Inject

class CommentDefaultRepository
    @Inject
    constructor(
        private val commentDataSource: CommentDataSource,
    ) : CommentRepository {
        override suspend fun fetchComments(staccatoId: Long): ApiResult<List<Comment>> =
            commentDataSource.getComments(staccatoId).handle { it.toDomain() }

        override suspend fun createComment(newComment: NewComment): ApiResult<Unit> =
            commentDataSource.createComment(
                newComment.toDto(),
            ).handle { Unit }

        override suspend fun updateComment(
            commentId: Long,
            content: String,
        ): ApiResult<Unit> =
            commentDataSource.updateComment(
                commentId,
                CommentUpdateRequest(content),
            ).handle { Unit }

        override suspend fun deleteComment(commentId: Long): ApiResult<Unit> = commentDataSource.deleteComment(commentId).handle { Unit }
    }
