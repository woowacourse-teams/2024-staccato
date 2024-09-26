package com.staccato.member.service.dto.response;

import com.staccato.member.domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;


@Schema(description = "프로필을 조회 했을 때 응답 형식입니다.")
public record MemberProfileResponse(
        @Schema(example = "staccato")
        String nickname,
        @Schema(example = "https://d1234abcdefg.cloudfront.net/staccato/image/abcdefg.jpg")
        String profileImageUrl,
        @Schema(example = "{UUID}")
        String code) {
    public MemberProfileResponse(Member member) {
        this(member.getNickname().getNickname(), member.getImageUrl(), member.getCode());
    }
}
