package com.staccato.moment.service.dto.response;

public record CommentShareResponse(
        String nickname,
        String content,
        String memberImageUrl
) {
}
