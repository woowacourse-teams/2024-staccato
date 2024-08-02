package com.staccato.auth.service;

import org.springframework.stereotype.Service;

import com.staccato.auth.service.request.LoginRequest;
import com.staccato.auth.service.response.LoginResponse;
import com.staccato.config.auth.TokenProvider;
import com.staccato.exception.StaccatoException;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;

    public LoginResponse login(LoginRequest loginRequest) {
        Member member = createMember(loginRequest);
        String token = tokenProvider.create(member);
        return new LoginResponse(token);
    }

    private Member createMember(LoginRequest loginRequest) {
        Member member = loginRequest.toMember();
        validateNickname(member.getNickname());
        return memberRepository.save(member);
    }

    private void validateNickname(String nickname) {
        if(memberRepository.existsByNickname(nickname)){
            throw new StaccatoException("이미 존재하는 닉네임입니다. 다시 설정해주세요.");
        }
    }
}
