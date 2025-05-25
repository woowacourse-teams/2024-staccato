package com.staccato.auth.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.staccato.auth.service.dto.request.LoginRequest;
import com.staccato.auth.service.dto.response.LoginResponse;
import com.staccato.category.domain.Category;
import com.staccato.category.repository.CategoryRepository;
import com.staccato.config.jwt.MemberTokenProvider;
import com.staccato.config.log.LogForm;
import com.staccato.config.log.annotation.Trace;
import com.staccato.exception.UnauthorizedException;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.member.service.MemberValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Trace
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final MemberTokenProvider tokenProvider;
    private final MemberValidator memberValidator;

    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {
        Member member = createMember(loginRequest);
        String token = tokenProvider.create(member);
        createBasicCategory(member);
        return new LoginResponse(token);
    }

    private Member createMember(LoginRequest loginRequest) {
        Member member = loginRequest.toMember();
        memberValidator.validateNicknameUniqueness(member.getNickname());
        return memberRepository.save(member);
    }

    private void createBasicCategory(Member member) {
        Category category = Category.basic(member);
        categoryRepository.save(category);
    }

    public Member extractFromToken(String token) {
        Member member = memberRepository.findById(tokenProvider.extractMemberId(token))
                .orElseThrow(UnauthorizedException::new);
        log.info(LogForm.LOGIN_MEMBER_FORM, member.getId(), member.getNickname().getNickname());
        return member;
    }

    public LoginResponse loginByCode(String code) {
        Member member = memberValidator.getMemberByCodeOrThrow(code);
        String token = tokenProvider.create(member);
        return new LoginResponse(token);
    }
}
