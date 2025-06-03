package com.staccato.member.service.dto.response;

import java.util.List;
import com.staccato.member.domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원 정보 목록에 대한 응답 형식입니다.")
public record MemberResponses(List<MemberResponse> members) {
    public static MemberResponses of(List<Member> members) {
        return new MemberResponses(members.stream()
                .map(MemberResponse::new)
                .toList());
    }
}
