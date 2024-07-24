package com.staccato.member.service.dto.response;

import java.util.List;

import com.staccato.member.domain.Member;

public record MemberResponses(List<MemberResponse> members) {
    public static MemberResponses from(List<Member> members) {
        List<MemberResponse> memberResponses = members.stream()
                .map(MemberResponse::new)
                .toList();
        return new MemberResponses(memberResponses);
    }
}
