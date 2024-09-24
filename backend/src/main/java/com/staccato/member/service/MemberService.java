package com.staccato.member.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.staccato.exception.ForbiddenException;
import com.staccato.exception.StaccatoException;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.member.service.dto.response.MemberProfileResponse;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    @Transactional
    public MemberProfileResponse changeProfileImage(Member member, long memberId, String imageUrl) {
        Member target = getMemberById(memberId);
        validate(member, target);
        target.updateImage(imageUrl);
        return new MemberProfileResponse(target.getImageUrl());
    }

    private Member getMemberById(long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new StaccatoException("요청하신 사용자 정보를 찾을 수 없어요."));
    }

    private void validate(Member member, Member target) {
        if (target.isNotSame(member)) {
            throw new ForbiddenException();
        }
    }
}
