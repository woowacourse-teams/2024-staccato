package com.staccato.member.service.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.staccato.member.domain.Member;

public record MemberResponse(
        Long memberId,
        String nickname,
        @JsonInclude(JsonInclude.Include.NON_NULL) String memberImage
) {
    public MemberResponse(Member member) {
        this(member.getId(), member.getNickname().getNickname(), member.getImageUrl());
    }
}
