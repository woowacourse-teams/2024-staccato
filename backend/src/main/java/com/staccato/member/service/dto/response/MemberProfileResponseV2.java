package com.staccato.member.service.dto.response;

import com.staccato.config.swagger.SwaggerExamples;
import com.staccato.member.domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;


@Schema(description = "프로필을 조회 했을 때 응답 형식입니다.")
public record MemberProfileResponseV2(
        @Schema(example = SwaggerExamples.MEMBER_ID)
        Long memberId,
        @Schema(example = SwaggerExamples.MEMBER_NICKNAME)
        String nickname,
        @Schema(example = SwaggerExamples.IMAGE_URL)
        String profileImageUrl,
        @Schema(example = SwaggerExamples.MEMBER_CODE)
        String code) {
    public MemberProfileResponseV2(Member member) {
        this(member.getId(), member.getNickname().getNickname(), member.getImageUrl(), member.getCode());
    }
}
