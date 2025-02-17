package com.staccato.config.jwt;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.staccato.exception.UnauthorizedException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class AbstractTokenProviderTest {
    private final static String TEST_SECRET_KEY = "test-secret-key";
    private final static String TEST_PAYLOAD = "test-payload";
    private final static String INVALID_TOKEN = "invalid.token.value";
    private final static String TEST_PROPERTY_NAME = "data";

    private StubTokenProvider tokenProvider;

    @BeforeEach
    public void setUp() {
        TokenProperties tokenProperties = new TokenProperties(TEST_SECRET_KEY);
        tokenProvider = new StubTokenProvider(tokenProperties);
    }

    @DisplayName("토큰을 생성할 수 있다.")
    @Test
    void testCreate() {
        // given & when
        String token = tokenProvider.create(TEST_PAYLOAD);

        // then
        assertThat(token).isNotNull();
    }

    @DisplayName("토큰의 Payload를 추출할 수 있다.")
    @Test
    void testGetPayload() {
        // given
        String token = tokenProvider.create(TEST_PAYLOAD);

        // when
        Claims claims = tokenProvider.getPayload(token);

        // then
        assertThat(claims.get(TEST_PROPERTY_NAME)).isEqualTo(TEST_PAYLOAD);
    }

    @DisplayName("토큰이 유효하면, 검증을 통과한다.")
    @Test
    void validateTokenSuccess() {
        // given
        String token = tokenProvider.create(TEST_PAYLOAD);

        // when & then
        tokenProvider.validateToken(token);
    }

    @DisplayName("만료된 토큰이면, 예외를 던진다.")
    @Test
    void validateTokenExpired() {
        // given
        String expiredToken = tokenProvider.createExpired(TEST_PAYLOAD);

        // when & then
        assertThatThrownBy(() -> tokenProvider.validateToken(expiredToken))
                .isInstanceOf(UnauthorizedException.class);
    }

    @DisplayName("잘못된 토큰이면, 예외를 던진다.")
    @Test
    void validateTokenInvalid() {
        // given & when & then
        assertThatThrownBy(() -> tokenProvider.validateToken(INVALID_TOKEN))
                .isInstanceOf(UnauthorizedException.class);
    }

    private static class StubTokenProvider extends AbstractTokenProvider {
        public StubTokenProvider(TokenProperties tokenProperties) {
            super(tokenProperties);
        }

        @Override
        public String create(Object payload) {
            return Jwts.builder()
                    .claim(TEST_PROPERTY_NAME, payload)
                    .signWith(SignatureAlgorithm.HS256, tokenProperties.secretKey().getBytes())
                    .compact();
        }

        public String createExpired(Object payload) {
            return Jwts.builder()
                    .claim(TEST_PROPERTY_NAME, payload)
                    .setExpiration(new Date(System.currentTimeMillis() - 1000))
                    .signWith(SignatureAlgorithm.HS256, tokenProperties.secretKey().getBytes())
                    .compact();
        }
    }
}
