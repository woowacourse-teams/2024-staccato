package com.staccato.auth.service.dto.request;

import java.util.Objects;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.staccato.config.swagger.SwaggerExamples;
import com.staccato.member.domain.Member;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원을 등록하기 위한 요청 형식입니다.")
public record LoginRequest(
        @Schema(example = SwaggerExamples.MEMBER_NICKNAME)
        @NotBlank(message = "닉네임을 입력해주세요.")
        String nickname
) {
    public LoginRequest {
        if (Objects.nonNull(nickname)) {
            nickname = nickname.trim();
        }
    }

    public Member toMember() {
        return Member.create(nickname);
    }
}
