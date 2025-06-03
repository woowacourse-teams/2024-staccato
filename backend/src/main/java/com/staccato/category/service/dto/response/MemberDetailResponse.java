package com.staccato.category.service.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.staccato.category.domain.CategoryMember;
import com.staccato.config.swagger.SwaggerExamples;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "카테고리 조회 시 함께하는 회원 정보를 표시할 때 필요한 정보에 대한 응답 형식입니다.")
public record MemberDetailResponse(
        @Schema(example = SwaggerExamples.MEMBER_ID)
        Long memberId,
        @Schema(example = SwaggerExamples.MEMBER_NICKNAME)
        String nickname,
        @Schema(example = SwaggerExamples.IMAGE_URL)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String memberImageUrl,
        @Schema(example = SwaggerExamples.CATEGORY_ROLE)
        String memberRole
) {
    public MemberDetailResponse(CategoryMember categoryMember) {
        this(
                categoryMember.getMember().getId(),
                categoryMember.getMember().getNickname().getNickname(),
                categoryMember.getMember().getImageUrl(),
                categoryMember.getRole().getRole()
        );
    }
}
