package com.woowacourse.staccato.data.dto.mapper

import com.woowacourse.staccato.data.dto.comment.CommentDto
import com.woowacourse.staccato.data.dto.comment.CommentRequest
import com.woowacourse.staccato.data.dto.comment.CommentsResponse
import com.woowacourse.staccato.domain.model.Comment
import com.woowacourse.staccato.domain.model.NewComment

fun CommentsResponse.toDomain(): List<Comment> =
    comments.map { it.toDomain() }

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
        momentId = momentId,
        content = content,
    )
