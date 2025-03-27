package com.staccato.config.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.staccato.exception.UnauthorizedException;
import com.staccato.fixture.member.MemberFixture;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import io.jsonwebtoken.Claims;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class TokenProviderTest {
    @MockBean
    private TokenProperties tokenProperties;
    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    private MemberRepository memberRepository;

    private Member member;
    private String secretKey = "my-secret-key";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(tokenProperties.secretKey()).thenReturn(secretKey);

        member = memberRepository.save(MemberFixture.create());
    }

    @DisplayName("주어진 사용자 정보로 토큰을 생성한다.")
    @Test
    public void createToken() {
        // given & when
        String token = tokenProvider.create(member);

        // then
        assertNotNull(token);
    }

    @DisplayName("주어진 토큰에서 payload를 추출한다.")
    @Test
    public void getPayloadFromToken() {
        // given
        String token = tokenProvider.create(member);

        // when
        Claims claims = tokenProvider.getPayload(token);

        // then
        assertAll(
                () -> assertNotNull(claims),
                () -> assertThat(claims.get("id", Long.class)).isEqualTo(member.getId()),
                () -> assertThat(claims.get("nickname")).isEqualTo(member.getNickname().getNickname()),
                () -> assertThat(claims.get("createdAt")).isEqualTo(member.getCreatedAt().toString())
        );
    }

    @DisplayName("주어진 토큰에서 사용자 식별자를 추출한다.")
    @Test
    public void testExtractMemberId() {
        // given
        String token = tokenProvider.create(member);

        // when
        long extractedId = tokenProvider.extractMemberId(token);

        // then
        assertThat(extractedId).isEqualTo(member.getId());
    }

    @DisplayName("주어진 토큰이 올바르지 않으면 인증 오류가 발생한다.")
    @Test
    public void cannotGetPayloadByInvalidToken() {
        // given
        String invalidToken = "invalid.token.value";

        // when & then
        assertThatThrownBy(() -> tokenProvider.getPayload(invalidToken))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("인증되지 않은 사용자입니다.");
    }
}
