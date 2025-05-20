package com.staccato.invitation.service.dto.response;

import com.staccato.config.swagger.SwaggerExamples;
import com.staccato.member.domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "초대 대상의 응답 형식입니다.")
public record InviteeResponse(
        @Schema(example = SwaggerExamples.MEMBER_ID)
        Long id,
        @Schema(example = SwaggerExamples.MEMBER_NICKNAME)
        String nickname,
        @Schema(example = SwaggerExamples.IMAGE_URL)
        String profileImageUrl
) {
    public InviteeResponse(Member invitee) {
        this(invitee.getId(), invitee.getNickname().getNickname(), invitee.getImageUrl());
    }
}
