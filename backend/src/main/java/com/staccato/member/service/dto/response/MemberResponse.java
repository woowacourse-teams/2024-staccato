package com.staccato.member.service.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.staccato.config.swagger.SwaggerExamples;
import com.staccato.member.domain.Member;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "여러 회원 정보를 표시할 때 필요한 정보에 대한 응답 형식입니다.")
public record MemberResponse(
        @Schema(example = SwaggerExamples.MEMBER_ID)
        Long memberId,
        @Schema(example = SwaggerExamples.MEMBER_NICKNAME)
        String nickname,
        @Schema(example = SwaggerExamples.IMAGE_URL)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String memberImageUrl
) {
    public MemberResponse(Member member) {
        this(member.getId(), member.getNickname().getNickname(), member.getImageUrl());
    }
}
