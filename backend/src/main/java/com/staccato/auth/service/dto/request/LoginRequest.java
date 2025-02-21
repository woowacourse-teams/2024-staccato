package com.staccato.auth.service.dto.request;

import java.util.Objects;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.staccato.member.domain.Member;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원을 등록하기 위한 요청 형식입니다.")
public record LoginRequest(
        @Schema(example = "hi_staccato")
        @NotNull(message = "닉네임을 입력해주세요.")
        @Size(min = 1, max = 20, message = "1자 이상 20자 이하의 닉네임으로 설정해주세요.")
        String nickname
) {
    public LoginRequest {
        if (Objects.nonNull(nickname)) {
            nickname = nickname.trim();
        }
    }

    public Member toMember() {
        return Member.create(nickname, "");
    }
}
