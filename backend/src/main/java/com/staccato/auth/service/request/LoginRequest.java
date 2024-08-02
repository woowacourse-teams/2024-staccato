package com.staccato.auth.service.request;

import com.staccato.member.domain.Member;

public record LoginRequest(String nickname) {
    public Member toMember() {
        return Member.builder()
                .nickname(nickname)
                .build();
    }
}
