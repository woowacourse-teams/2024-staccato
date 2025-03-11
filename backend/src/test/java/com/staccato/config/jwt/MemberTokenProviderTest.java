package com.staccato.config.jwt;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.staccato.exception.UnauthorizedException;
import com.staccato.fixture.Member.MemberFixture;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import io.jsonwebtoken.Claims;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.failBecauseExceptionWasNotThrown;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class MemberTokenProviderTest {

    @Autowired
    private MemberRepository memberRepository;

    private MemberTokenProvider tokenProvider;
    private Member member;

    @BeforeEach
    public void setUp() {
        TokenProperties tokenProperties = new TokenProperties("test-secret-key");
        tokenProvider = new MemberTokenProvider(tokenProperties);
        member = memberRepository.save(MemberFixture.create());
    }

    @DisplayName("멤버 토큰은 멤버 아이디를 갖고 있다.")
    @Test
    void testShareTokenContainsStaccatoId() {
        // given
        String token = tokenProvider.create(member);

        // when
        Claims claims = tokenProvider.getPayload(token);

        // then
        Assertions.assertThat(claims.get("id", Long.class)).isEqualTo(member.getId());
    }

    @DisplayName("멤버 토큰에서 멤버 아이디를 추출할 수 있다.")
    @Test
    void testExtractStaccatoId() {
        // given
        String token = tokenProvider.create(member);

        // when
        long memberId = tokenProvider.extractMemberId(token);

        // then
        Assertions.assertThat(memberId).isEqualTo(member.getId());
    }
}
