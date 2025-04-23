package com.on.staccato.data.comment

import com.on.staccato.data.dto.comment.CommentRequest
import com.on.staccato.data.dto.comment.CommentUpdateRequest
import com.on.staccato.data.dto.comment.CommentsResponse
import com.on.staccato.data.network.ApiResult
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
        @Query(STACCATO_ID) staccatoId: Long,
    ): ApiResult<CommentsResponse>

    @POST(COMMENTS_PATH)
    suspend fun postComment(
        @Body commentRequest: CommentRequest,
    ): ApiResult<Unit>

    @PUT(COMMENTS_PATH_WITH_COMMENT_ID)
    suspend fun putComment(
        @Path(COMMENT_ID) commentId: Long,
        @Body commentUpdateRequest: CommentUpdateRequest,
    ): ApiResult<Unit>

    @DELETE(COMMENTS_PATH_WITH_COMMENT_ID)
    suspend fun deleteComment(
        @Path(COMMENT_ID) commentId: Long,
    ): ApiResult<Unit>

    companion object {
        private const val COMMENTS_PATH = "/comments"
        private const val STACCATO_ID = "staccatoId"
        private const val COMMENT_ID = "commentId"
        private const val COMMENTS_PATH_WITH_COMMENT_ID = "$COMMENTS_PATH/{$COMMENT_ID}"
    }
}
