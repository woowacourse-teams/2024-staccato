package com.staccato.auth.service;

import com.staccato.category.repository.CategoryMemberRepository;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.staccato.ServiceSliceTest;
import com.staccato.auth.service.dto.request.LoginRequest;
import com.staccato.auth.service.dto.response.LoginResponse;
import com.staccato.config.jwt.MemberTokenProvider;
import com.staccato.exception.StaccatoException;
import com.staccato.exception.UnauthorizedException;
import com.staccato.fixture.auth.LoginRequestFixtures;
import com.staccato.fixture.member.MemberFixtures;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class AuthServiceTest extends ServiceSliceTest {

    @Autowired
    private AuthService authService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CategoryMemberRepository categoryMemberRepository;
    @Autowired
    private MemberTokenProvider tokenProvider;

    @DisplayName("입력받은 닉네임으로 멤버를 저장하고, 토큰을 생성한다.")
    @Test
    void login() {
        // given
        LoginRequest loginRequest = LoginRequestFixtures.defaultLoginRequest().build();

        // when
        LoginResponse loginResponse = authService.login(loginRequest);

        // then
        assertAll(
            () -> assertThat(memberRepository.findAll()).hasSize(1),
            () -> assertThat(loginResponse.token()).isNotNull()
        );
    }

    @DisplayName("입력받은 닉네임으로 멤버를 저장할 때 기본 카테고리를 생성한다.")
    @Test
    void loginThenCreateBasicCategory() {
        // given
        LoginRequest loginRequest = LoginRequestFixtures.defaultLoginRequest().build();

        // when
        LoginResponse loginResponse = authService.login(loginRequest);

        // then
        assertAll(
            () -> assertThat(memberRepository.findAll()).hasSize(1),
            () -> assertThat(loginResponse.token()).isNotNull(),
            () -> assertThat(categoryMemberRepository.findAll()).hasSize(1)
        );
    }

    @DisplayName("입력받은 닉네임이 이미 존재하는 닉네임인 경우 예외가 발생한다.")
    @Test
    void cannotLoginByDuplicated() {
        // given
        MemberFixtures.defaultMember()
                .withNickname("nickname").buildAndSave(memberRepository);
        LoginRequest loginRequest = LoginRequestFixtures.defaultLoginRequest()
                .withNickname("nickname").build();

        // when & then
        assertThatThrownBy(() -> authService.login(loginRequest))
            .isInstanceOf(StaccatoException.class)
            .hasMessage("이미 존재하는 닉네임입니다. 다시 설정해주세요.");
    }

    @DisplayName("만약 전달 받은 토큰이 null일 경우 예외가 발생한다.")
    @Test
    void cannotExtractMemberByUnknown() {
        // given
        MemberFixtures.defaultMember().buildAndSave(memberRepository);

        // when & then
        assertThatThrownBy(() -> authService.extractFromToken(null))
            .isInstanceOf(UnauthorizedException.class)
            .hasMessage("인증되지 않은 사용자입니다.");
    }

    @DisplayName("고유 코드로 사용자를 조회해서 토큰을 발급한다.")
    @Test
    void createTokenByCode() {
        // given
        String code = UUID.randomUUID().toString();
        Member member = MemberFixtures.defaultMember()
                .withCode(code).buildAndSave(memberRepository);

        // when
        LoginResponse loginResponse = authService.loginByCode(code);

        // then
        assertThat(tokenProvider.extractMemberId(loginResponse.token())).isEqualTo(member.getId());
    }

    @DisplayName("존재하지 않는 고유 코드로 사용자를 조회하면 예외가 발생한다.")
    @Test
    void cannotCreateTokenByCode() {
        // given
        String code = UUID.randomUUID().toString();

        // when
        assertThatThrownBy(() -> authService.loginByCode(code))
            .isInstanceOf(StaccatoException.class)
            .hasMessage("유효하지 않은 코드입니다. 올바른 코드인지 확인해주세요.");
    }
}
