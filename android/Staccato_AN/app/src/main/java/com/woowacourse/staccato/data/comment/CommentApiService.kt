package com.woowacourse.staccato.data.comment

import com.woowacourse.staccato.data.comment.CommentApiService.Companion.COMMENTS_PATH_WITH_ID
import com.woowacourse.staccato.data.dto.comment.CommentRequest
import com.woowacourse.staccato.data.dto.comment.CommentUpdateRequest
import com.woowacourse.staccato.data.dto.comment.CommentsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface CommentApiService {
    @GET(COMMENTS_PATH)
    suspend fun getComments(
        @Query("momentId") momentId: Long,
    ): Response<CommentsResponse>

    @POST(COMMENTS_PATH)
    suspend fun postComments(
        @Body commentRequest: CommentRequest,
    ): Response<Unit>

    @PUT(COMMENTS_PATH)
    suspend fun putComment(
        @Query("commentId") commentId: Long,
        @Body commentUpdateRequest: CommentUpdateRequest,
    ): Response<Unit>

    @DELETE(COMMENTS_PATH)
    suspend fun deleteComment(
        @Query("commentId") commentId: Long,
    ): Response<Unit>

    companion object {
        private const val COMMENTS_PATH = "/comments"
        private const val COMMENT_ID = "/{commentId}"
        private const val COMMENTS_PATH_WITH_ID = "$COMMENTS_PATH$COMMENT_ID"
    }
}
