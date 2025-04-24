package com.staccato.member.service.dto.response;

import com.staccato.config.swagger.SwaggerExamples;
import com.staccato.member.domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;


@Schema(description = "프로필을 조회 했을 때 응답 형식입니다.")
public record MemberProfileResponse(
        @Schema(example = SwaggerExamples.MEMBER_NICKNAME)
        String nickname,
        @Schema(example = SwaggerExamples.IMAGE_URL)
        String profileImageUrl,
        @Schema(example = SwaggerExamples.MEMBER_CODE)
        String code) {
    public MemberProfileResponse(Member member) {
        this(member.getNickname().getNickname(), member.getImageUrl(), member.getCode());
    }
}
