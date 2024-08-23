package com.woowacourse.staccato.data.comment

import com.woowacourse.staccato.data.ApiResponseHandler.handleApiResponse
import com.woowacourse.staccato.data.ResponseResult
import com.woowacourse.staccato.data.StaccatoClient
import com.woowacourse.staccato.data.dto.comment.CommentRequest
import com.woowacourse.staccato.data.dto.comment.CommentUpdateRequest
import com.woowacourse.staccato.data.dto.comment.CommentsResponse

class CommentRemoteDataSource(
    private val commentApiService: CommentApiService = StaccatoClient.commentApiService,
) : CommentDataSource {
    override suspend fun getComments(momentId: Long): ResponseResult<CommentsResponse> =
        handleApiResponse { commentApiService.getComments(momentId) }

    override suspend fun createComment(commentRequest: CommentRequest): ResponseResult<Unit> =
        handleApiResponse { commentApiService.postComments(commentRequest) }

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
