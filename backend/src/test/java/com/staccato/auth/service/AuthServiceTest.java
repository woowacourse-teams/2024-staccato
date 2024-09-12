package com.staccato.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.staccato.ServiceSliceTest;
import com.staccato.auth.service.dto.request.LoginRequest;
import com.staccato.auth.service.dto.response.LoginResponse;
import com.staccato.exception.StaccatoException;
import com.staccato.exception.UnauthorizedException;
import com.staccato.fixture.Member.MemberFixture;
import com.staccato.fixture.auth.LoginRequestFixture;
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
        LoginRequest loginRequest = LoginRequestFixture.create();

        // when
        LoginResponse loginResponse = authService.login(loginRequest);

        // then
        assertAll(
                () -> assertThat(memberRepository.findAll()).hasSize(1),
                () -> assertThat(loginResponse.token()).isNotNull()
        );
    }

    @DisplayName("입력받은 닉네임이 이미 존재하는 닉네임인 경우 기존에 존재하던 사용자를 반환한다.")
    @Test
    void loginByDuplicated() {
        // given
        memberRepository.save(MemberFixture.create());
        LoginRequest loginRequest = LoginRequestFixture.create();

        // when
        LoginResponse response = authService.login(loginRequest);

        // then
        assertThat(response.token()).isNotBlank();
    }

    @Disabled
    @DisplayName("입력받은 닉네임이 이미 존재하는 닉네임인 경우 예외가 발생한다.")
    @Test
    void cannotLoginByDuplicated() {
        // given
        memberRepository.save(MemberFixture.create());
        LoginRequest loginRequest = LoginRequestFixture.create();

        // when & then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("이미 존재하는 닉네임입니다. 다시 설정해주세요.");
    }

    @DisplayName("만약 전달 받은 토큰이 null일 경우 예외가 발생한다.")
    @Test
    void cannotExtractMemberByUnknown() {
        // given
        memberRepository.save(MemberFixture.create());

        // when & then
        assertThatThrownBy(() -> authService.extractFromToken(null))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("인증되지 않은 사용자입니다.");
    }
}
