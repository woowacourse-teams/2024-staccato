package com.staccato.member.service.dto.request;

import com.staccato.config.swagger.SwaggerExamples;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원 조회 요청")
public record MemberReadRequest(
        @Schema(description = "검색어", example = SwaggerExamples.MEMBER_NICKNAME)
        String nickname,
        @Schema(description = "검색에서 제외 할 카테고리 식별자", example = SwaggerExamples.CATEGORY_ID)
        long excludeCategoryId
) {
    public String trimmedNickname() {
        return nickname.trim();
    }
}
