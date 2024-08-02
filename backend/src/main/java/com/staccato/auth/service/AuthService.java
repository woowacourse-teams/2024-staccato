package com.staccato.auth.service;

import org.springframework.stereotype.Service;

import com.staccato.auth.service.dto.request.LoginRequest;
import com.staccato.auth.service.dto.response.LoginResponse;
import com.staccato.config.auth.TokenProvider;
import com.staccato.exception.StaccatoException;
import com.staccato.exception.UnauthorizedException;
import com.staccato.member.domain.Member;
import com.staccato.member.domain.Nickname;
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

    private void validateNickname(Nickname nickname) {
        if (memberRepository.existsByNickname(nickname)) {
            throw new StaccatoException("이미 존재하는 닉네임입니다. 다시 설정해주세요.");
        }
    }

    public Member extractFromToken(String token) {
        return memberRepository.findById(tokenProvider.extractMemberId(token))
                .orElseThrow(UnauthorizedException::new);
    }
}
