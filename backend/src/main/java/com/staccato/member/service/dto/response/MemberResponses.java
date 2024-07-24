package com.staccato.member.service.dto.response;

import java.util.List;

import com.staccato.member.domain.Member;

public record MemberResponses(List<MemberResponse> members) {
    public static MemberResponses from(List<Member> members) {
        return new MemberResponses(members.stream()
                .map(MemberResponse::new)
                .toList());
    }
}
