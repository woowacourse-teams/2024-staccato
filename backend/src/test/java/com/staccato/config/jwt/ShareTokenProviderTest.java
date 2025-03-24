package com.staccato.config.jwt;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.staccato.config.jwt.dto.ShareTokenPayload;

import io.jsonwebtoken.Claims;

public class ShareTokenProviderTest {
    private static final String TEST_SECRET_KEY = "test-secret-key";
    private static final long STACCATO_ID = 1L;
    private static final long MEMBER_ID = 1L;

    private static ShareTokenProvider tokenProvider;
    private static String shareToken;

    @BeforeAll
    static void init() {
        TokenProperties tokenProperties = new TokenProperties(TEST_SECRET_KEY);
        tokenProvider = new ShareTokenProvider(tokenProperties);
        shareToken = tokenProvider.create(new ShareTokenPayload(STACCATO_ID, MEMBER_ID));
    }

    @DisplayName("공유 토큰은 스타카토 아이디와 멤버 아이디를 갖고 있다.")
    @Test
    void shareTokenContainsStaccatoId() {
        // given & when
        Claims claims = tokenProvider.getPayload(shareToken);

        // then
        assertThat(claims.get("staccatoId", Long.class)).isEqualTo(STACCATO_ID);
        assertThat(claims.get("memberId", Long.class)).isEqualTo(MEMBER_ID);
    }

    @DisplayName("공유 토큰은 만료 기한을 갖고 있다.")
    @Test
    void shareTokenContainsStaccatoExpiration() {
        // given & when
        Claims claims = tokenProvider.getPayload(shareToken);

        // then
        assertThat(claims.getExpiration()).isNotNull();
    }

    @DisplayName("공유 토큰에서 스타카토 아이디를 추출할 수 있다.")
    @Test
    void extractStaccatoId() {
        // given & when
        long staccatoId = tokenProvider.extractStaccatoId(shareToken);

        // then
        assertThat(staccatoId).isEqualTo(STACCATO_ID);
    }

    @DisplayName("공유 토큰에서 멤버 아이디를 추출할 수 있다.")
    @Test
    void extractMemberId() {
        // given & when
        long memberId = tokenProvider.extractMemberId(shareToken);

        // then
        assertThat(memberId).isEqualTo(MEMBER_ID);
    }

    @DisplayName("공유 토큰에서 만료 기한을 추출할 수 있다.")
    @Test
    void extractExpiredAt() {
        // given & when
        LocalDateTime expiredAt = tokenProvider.extractExpiredAt(shareToken);

        // then
        assertThat(expiredAt).isNotNull();
    }
}
