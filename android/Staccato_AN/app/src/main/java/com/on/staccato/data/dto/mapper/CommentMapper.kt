package com.on.staccato.data.dto.mapper

import com.on.staccato.data.dto.comment.CommentDto
import com.on.staccato.data.dto.comment.CommentRequest
import com.on.staccato.data.dto.comment.CommentsResponse
import com.on.staccato.domain.model.Comment
import com.on.staccato.domain.model.NewComment

fun CommentsResponse.toDomain(): List<Comment> = comments.map { it.toDomain() }

fun CommentDto.toDomain(): Comment =
    Comment(
        commentId = commentId,
        memberId = memberId,
        nickname = nickname,
        memberImageUrl = memberImageUrl,
        content = content,
    )

fun NewComment.toDto(): CommentRequest =
    CommentRequest(
        staccatoId = staccatoId,
        content = content,
    )
