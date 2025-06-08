package com.staccato.member.service.dto.response;

import java.util.List;
import com.staccato.member.domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "검색된 회원 정보 목록에 대한 응답 형식입니다.")
public record MemberSearchResponses(List<MemberSearchResponse> members) {
    public static MemberSearchResponses none(List<Member> members) {
        return new MemberSearchResponses(members.stream()
                .map(MemberSearchResponse::none)
                .toList());
    }

    public static MemberSearchResponses empty() {
        return new MemberSearchResponses(List.of());
    }

}
