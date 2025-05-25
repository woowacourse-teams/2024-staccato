package com.staccato.member.service;

import org.springframework.stereotype.Component;

import com.staccato.exception.StaccatoException;
import com.staccato.member.domain.Member;
import com.staccato.member.domain.Nickname;
import com.staccato.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MemberValidator {

    private final MemberRepository memberRepository;

    public Member getMemberByCodeOrThrow(String code) {
        return memberRepository.findByCode(code)
                .orElseThrow(() -> new StaccatoException("유효하지 않은 코드입니다. 올바른 코드인지 확인해주세요."));
    }

    public Member getMemberByIdOrThrow(long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new StaccatoException("요청하신 사용자 정보를 찾을 수 없어요."));
    }

    public void validateNicknameUniqueness(Nickname nickname) {
        if (memberRepository.existsByNickname(nickname)) {
            throw new StaccatoException("이미 존재하는 닉네임입니다. 다시 설정해주세요.");
        }
    }
}
