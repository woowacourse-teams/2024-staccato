package com.staccato.auth.service;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.staccato.auth.service.dto.request.LoginRequest;
import com.staccato.auth.service.dto.response.LoginResponse;
import com.staccato.config.auth.AdminProperties;
import com.staccato.config.auth.TokenProvider;
import com.staccato.config.log.LogForm;
import com.staccato.config.log.annotation.Trace;
import com.staccato.exception.StaccatoException;
import com.staccato.exception.UnauthorizedException;
import com.staccato.member.domain.Member;
import com.staccato.member.domain.Nickname;
import com.staccato.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Trace
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@EnableConfigurationProperties(AdminProperties.class)
public class AuthService {
    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;
    private final AdminProperties adminProperties;
    private final Environment environment;

    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {
        if (isNotProdProfile() && adminProperties.key().equals(loginRequest.nickname())) {
            return new LoginResponse(adminProperties.token());
        }
        Member member = createMember(loginRequest);
        String token = tokenProvider.create(member);
        return new LoginResponse(token);
    }

    private boolean isNotProdProfile() {
        return !environment.acceptsProfiles(Profiles.of("prod"));
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
        Member member = memberRepository.findById(tokenProvider.extractMemberId(token))
                .orElseThrow(UnauthorizedException::new);
        log.info(LogForm.LOGIN_MEMBER_FORM, member.getId(), member.getNickname().getNickname());
        return member;
    }

    public LoginResponse loginByCode(String code) {
        Member member = getMemberByCode(code);
        String token = tokenProvider.create(member);
        return new LoginResponse(token);
    }

    private Member getMemberByCode(String code) {
        return memberRepository.findByCode(code)
                .orElseThrow(() -> new StaccatoException("유효하지 않은 코드입니다. 올바른 코드인지 확인해주세요."));
    }
}
