package com.staccato.config.jwt;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.jsonwebtoken.Claims;

public class ShareTokenProviderTest {
    private final static String TEST_SECRET_KEY = "test-secret-key";
    private final static long STACCATO_ID = 1L;
    private final static String PROPERTY_NAME = "staccatoId";

    private ShareTokenProvider tokenProvider;

    @BeforeEach
    public void setUp() {
        TokenProperties tokenProperties = new TokenProperties(TEST_SECRET_KEY);
        tokenProvider = new ShareTokenProvider(tokenProperties);
    }

    @DisplayName("공유 토큰은 스타카토 아이디를 갖고 있다.")
    @Test
    void testShareTokenContainsStaccatoId() {
        // given
        String token = tokenProvider.create(STACCATO_ID);

        // when
        Claims claims = tokenProvider.getPayload(token);

        // then
        assertThat(claims.get(PROPERTY_NAME, Long.class)).isEqualTo(STACCATO_ID);
    }

    @DisplayName("공유 토큰은 만료 기한을 갖고 있다.")
    @Test
    void testShareTokenContainsStaccatoExpiration() {
        // given
        String token = tokenProvider.create(STACCATO_ID);

        // when
        Claims claims = tokenProvider.getPayload(token);

        // then
        assertThat(claims.getExpiration()).isNotNull();
    }

    @DisplayName("공유 토큰에서 스타카토 아이디를 추출할 수 있다.")
    @Test
    void testExtractStaccatoId() {
        // given
        String token = tokenProvider.create(STACCATO_ID);

        // when
        long staccatoId = tokenProvider.extractStaccatoId(token);

        // then
        assertThat(staccatoId).isEqualTo(STACCATO_ID);
    }

    @DisplayName("토큰에서 만료 기한을 추출할 수 있다.")
    @Test
    void extractExpiredAt() {
        String token = tokenProvider.create(STACCATO_ID);
        LocalDateTime expiredAt = tokenProvider.extractExpiredAt(token);

        assertThat(expiredAt).isNotNull();
    }
}
