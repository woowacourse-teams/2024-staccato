package com.staccato.auth.service;

import com.staccato.category.domain.Category;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.staccato.auth.service.dto.request.LoginRequest;
import com.staccato.auth.service.dto.response.LoginResponse;
import com.staccato.category.domain.Role;
import com.staccato.config.jwt.MemberTokenProvider;
import com.staccato.config.log.LogForm;
import com.staccato.config.log.annotation.Trace;
import com.staccato.exception.StaccatoException;
import com.staccato.exception.UnauthorizedException;
import com.staccato.member.domain.Member;
import com.staccato.member.domain.Nickname;
import com.staccato.member.repository.MemberRepository;
import com.staccato.category.repository.CategoryRepository;
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

    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {
        Member member = createMember(loginRequest);
        String token = tokenProvider.create(member);
        createBasicCategory(member);
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

    private void createBasicCategory(Member member) {
        Category category = Category.basic(member.getNickname());
        category.addCategoryMember(member, Role.HOST);
        categoryRepository.save(category);
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
