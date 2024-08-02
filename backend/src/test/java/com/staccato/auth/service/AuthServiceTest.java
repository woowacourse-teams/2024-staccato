package com.staccato.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.staccato.ServiceSliceTest;
import com.staccato.auth.service.request.LoginRequest;
import com.staccato.exception.StaccatoException;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;

class AuthServiceTest extends ServiceSliceTest {
    @Autowired
    private AuthService authService;
    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("입력받은 닉네임으로 멤버를 저장하고, 토큰을 생성한다.")
    @Test
    void login() {
        // given
        String nickname = "staccato";
        LoginRequest loginRequest = new LoginRequest(nickname);

        // when
        authService.login(loginRequest);

        // then
        assertThat(memberRepository.findAll()).hasSize(1);
    }

    @DisplayName("입력받은 닉네임이 이미 존재하는 닉네임인 경우 예외가 발생한다.")
    @Test
    void cannotLogin() {
        // given
        String nickname = "staccato";
        memberRepository.save(Member.builder().nickname(nickname).build());
        LoginRequest loginRequest = new LoginRequest(nickname);

        // when
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("이미 존재하는 닉네임입니다. 다시 설정해주세요.");
    }
}
