package com.on.staccato.data.comment

import com.on.staccato.data.ResponseResult
import com.on.staccato.data.dto.comment.CommentRequest
import com.on.staccato.data.dto.comment.CommentUpdateRequest
import com.on.staccato.data.dto.comment.CommentsResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface CommentApiService {
    @GET(COMMENTS_URI)
    suspend fun getComments(
        @Query(STACCATO_ID) staccatoId: Long,
    ): ResponseResult<CommentsResponse>

    @POST(COMMENTS_URI)
    suspend fun postComment(
        @Body commentRequest: CommentRequest,
    ): ResponseResult<Unit>

    @PUT(COMMENTS_URI_WITH_COMMENT_ID)
    suspend fun putComment(
        @Path(COMMENT_ID) commentId: Long,
        @Body commentUpdateRequest: CommentUpdateRequest,
    ): ResponseResult<Unit>

    @DELETE(COMMENTS_URI_WITH_COMMENT_ID)
    suspend fun deleteComment(
        @Path(COMMENT_ID) commentId: Long,
    ): ResponseResult<Unit>

    companion object {
        private const val COMMENTS_URI = "/comments"
        private const val STACCATO_ID = "momentId"
        private const val COMMENT_ID = "commentId"
        private const val COMMENTS_URI_WITH_COMMENT_ID = "$COMMENTS_URI/v2/{$COMMENT_ID}"
    }
}
