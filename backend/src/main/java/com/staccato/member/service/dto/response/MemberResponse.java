package com.staccato.member.service.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.staccato.member.domain.Member;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "여러 회원 정보를 표시할 때 필요한 정보에 대한 응답 형식입니다.")
public record MemberResponse(
        @Schema(example = "1")
        Long memberId,
        @Schema(example = "staccato")
        String nickName,
        @Schema(example = "https://example.com/members/profile.jpg")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String memberImage
) {
    public MemberResponse(Member member) {
        this(member.getId(), member.getNickname().getNickname(), member.getImageUrl());
    }
}
