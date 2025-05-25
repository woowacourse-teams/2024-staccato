package com.staccato.member.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.member.service.dto.response.MemberProfileImageResponse;
import com.staccato.member.service.dto.response.MemberResponses;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final MemberValidator memberValidator;

    @Transactional
    public MemberProfileImageResponse changeProfileImage(Member member, String imageUrl) {
        Member target = memberValidator.getMemberByIdOrThrow(member.getId());
        target.updateImage(imageUrl);
        return new MemberProfileImageResponse(target.getImageUrl());
    }

    public MemberResponses readMembersByNickname(Member member, String nickname) {
        List<Member> members = memberRepository.findByNicknameNicknameContainsAndIdNot(nickname, member.getId());
        return MemberResponses.of(members);
    }
}
