package com.staccato.member.service.dto.response;

import com.staccato.config.swagger.SwaggerExamples;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "프로필 사진을 변경 했을 때 응답 형식입니다.")
public record MemberProfileImageResponse(
        @Schema(example = SwaggerExamples.IMAGE_URL)
        String profileImageUrl
) {
}
