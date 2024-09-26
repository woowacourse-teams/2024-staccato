package com.staccato.member.service.dto.response;

import com.staccato.member.domain.Member;

public record MemberProfileResponse(String nickname, String profileImageUrl, String code) {
    public MemberProfileResponse(Member member) {
        this(member.getNickname().getNickname(), member.getImageUrl(), member.getCode());
    }
}
