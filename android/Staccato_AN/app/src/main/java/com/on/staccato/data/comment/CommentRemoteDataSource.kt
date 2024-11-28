package com.on.staccato.data.comment

import com.on.staccato.data.ApiResult
import com.on.staccato.data.dto.comment.CommentRequest
import com.on.staccato.data.dto.comment.CommentUpdateRequest
import com.on.staccato.data.dto.comment.CommentsResponse
import javax.inject.Inject

class CommentRemoteDataSource
    @Inject
    constructor(
        private val commentApiService: CommentApiService,
    ) : CommentDataSource {
        override suspend fun getComments(staccatoId: Long): ApiResult<CommentsResponse> = commentApiService.getComments(staccatoId)

        override suspend fun createComment(commentRequest: CommentRequest): ApiResult<Unit> =
            commentApiService.postComment(
                commentRequest,
            )

        override suspend fun updateComment(
            commentId: Long,
            commentUpdateRequest: CommentUpdateRequest,
        ): ApiResult<Unit> =
            commentApiService.putComment(
                commentId,
                commentUpdateRequest,
            )

        override suspend fun deleteComment(commentId: Long): ApiResult<Unit> = commentApiService.deleteComment(commentId)
    }
