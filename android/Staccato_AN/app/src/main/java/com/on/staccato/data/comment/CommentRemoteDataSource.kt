package com.on.staccato.data.comment

import com.on.staccato.data.ApiResponseHandler.handleApiResponse
import com.on.staccato.data.ResponseResult
import com.on.staccato.data.StaccatoClient
import com.on.staccato.data.dto.comment.CommentRequest
import com.on.staccato.data.dto.comment.CommentUpdateRequest
import com.on.staccato.data.dto.comment.CommentsResponse

class CommentRemoteDataSource(
    private val commentApiService: CommentApiService = StaccatoClient.commentApiService,
) : CommentDataSource {
    override suspend fun getComments(momentId: Long): ResponseResult<CommentsResponse> =
        handleApiResponse { commentApiService.getComments(momentId) }

    override suspend fun createComment(commentRequest: CommentRequest): ResponseResult<Unit> =
        handleApiResponse { commentApiService.postComment(commentRequest) }

    override suspend fun updateComment(
        commentId: Long,
        commentUpdateRequest: CommentUpdateRequest,
    ): ResponseResult<Unit> =
        handleApiResponse {
            commentApiService.putComment(
                commentId,
                commentUpdateRequest,
            )
        }

    override suspend fun deleteComment(commentId: Long): ResponseResult<Unit> =
        handleApiResponse { commentApiService.deleteComment(commentId) }
}
