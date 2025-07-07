package com.staccato.auth.service.dto.response;

import com.staccato.config.swagger.SwaggerExamples;
import com.staccato.member.domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원 등록 시 발급되는 토큰에 대한 응답 형식입니다.")
public record LoginResponseV2(
        @Schema(example = SwaggerExamples.MEMBER_ID)
        Long memberId,
        @Schema(example = SwaggerExamples.TOKEN)
        String token
) {
    public LoginResponseV2(Member member, String token) {
        this(member.getId(), token);
    }

    public LoginResponse toLoginResponse() {
        return new LoginResponse(token);
    }
}
