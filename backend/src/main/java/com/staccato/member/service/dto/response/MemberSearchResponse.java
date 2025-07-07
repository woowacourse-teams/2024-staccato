package com.staccato.member.service.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.staccato.config.swagger.SwaggerExamples;
import com.staccato.member.domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;

public record MemberSearchResponse(
        @Schema(example = SwaggerExamples.MEMBER_ID)
        Long memberId,
        @Schema(example = SwaggerExamples.MEMBER_NICKNAME)
        String nickname,
        @Schema(example = SwaggerExamples.IMAGE_URL)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String memberImageUrl,
        @Schema(description = "검색된 사용자가 카테고리에 이미 속한 사용자인지, 초대 요청을 받은 사용자인지, 그 외인지를 나타냅니다. (JOINED/REQUESTED/NONE)", example = SwaggerExamples.SEARCHED_MEMBER_STATUS)
        String status
) {
    public static MemberSearchResponse requested(Member member) {
        return new MemberSearchResponse(member.getId(), member.getNickname()
                .getNickname(), member.getImageUrl(), SearchedStatus.REQUESTED.name());
    }

    public static MemberSearchResponse joined(Member member) {
        return new MemberSearchResponse(member.getId(), member.getNickname()
                .getNickname(), member.getImageUrl(), SearchedStatus.JOINED.name());
    }

    public static MemberSearchResponse none(Member member) {
        return new MemberSearchResponse(member.getId(), member.getNickname()
                .getNickname(), member.getImageUrl(), SearchedStatus.NONE.name());
    }
}
