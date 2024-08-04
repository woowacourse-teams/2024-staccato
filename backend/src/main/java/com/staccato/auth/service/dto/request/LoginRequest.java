package com.staccato.auth.service.dto.request;

import jakarta.validation.constraints.NotNull;

import com.staccato.member.domain.Member;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원을 등록하기 위한 요청 형식입니다.")
public record LoginRequest(
        @Schema(example = "hi_staccato")
        @NotNull(message = "닉네임을 입력해주세요.")
        String nickname
) {
    public Member toMember() {
        return Member.builder()
                .nickname(nickname)
                .build();
    }
}
