package com.staccato.auth.service.request;

import jakarta.validation.constraints.NotNull;

import com.staccato.member.domain.Member;

public record LoginRequest(@NotNull(message = "닉네임을 입력해주세요.")
                           String nickname) {
    public Member toMember() {
        return Member.builder()
                .nickname(nickname)
                .build();
    }
}
