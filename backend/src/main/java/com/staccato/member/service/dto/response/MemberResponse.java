package com.staccato.member.service.dto.response;

import com.staccato.member.domain.Member;

public record MemberResponse(
        Long memberId,
        String nickName,
        String memberImage
) {
    public MemberResponse(Member member) {
        this(member.getId(), member.getNickname(), member.getImageUrl());
    }
}
