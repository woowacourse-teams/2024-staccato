package com.staccato.moment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.staccato.ServiceSliceTest;
import com.staccato.config.jwt.ShareTokenProvider;
import com.staccato.moment.service.dto.response.StaccatoShareLinkResponse;

import io.jsonwebtoken.Claims;

class StaccatoServiceTest extends ServiceSliceTest {
    private static final Long STACCATO_ID = 1L;

    @Autowired
    private StaccatoService staccatoService;
    @Autowired
    private ShareTokenProvider shareTokenProvider;

    @DisplayName("올바른 형식의 공유 링크를 생성할 수 있다.")
    @Test
    void createValidShareLink() {
        // given & when
        StaccatoShareLinkResponse response = staccatoService.createStaccatoShareLink(STACCATO_ID);

        // then
        assertThat(response.shareLink()).startsWith("https://staccato.kr/staccatos?token=");
    }

    @DisplayName("공유 링크는 JWT 토큰을 포함하고 있다.")
    @Test
    void shouldContainTokenInShareLink() {
        // given & when
        StaccatoShareLinkResponse response = staccatoService.createStaccatoShareLink(STACCATO_ID);
        String token = response.shareLink().split("token=")[1];

        // then
        assertThat(token).isNotNull();
    }

    @DisplayName("토큰은 올바른 스타카토 id를 포함하고 있다.")
    @Test
    void shouldContainStaccatoIdInToken() {
        // given
        StaccatoShareLinkResponse response = staccatoService.createStaccatoShareLink(STACCATO_ID);
        String token = response.shareLink().split("token=")[1];

        // when & then
        assertThatCode(() -> shareTokenProvider.extractStaccatoId(token))
                        .doesNotThrowAnyException();
        assertThat(shareTokenProvider.extractStaccatoId(token)).isEqualTo(STACCATO_ID);
    }

    @DisplayName("토큰은 만료 기한을 포함하고 있다.")
    void shouldContainExpirationInToken() {
        // give
        StaccatoShareLinkResponse response = staccatoService.createStaccatoShareLink(STACCATO_ID);
        String token = response.shareLink().split("token=")[1];

        // when
        Claims claims = shareTokenProvider.getPayload(token);

        // then
        assertThat(claims.getExpiration()).isNotNull();
    }

    @DisplayName("토큰이 유효하다.")
    void validateToken() {
        // given
        StaccatoShareLinkResponse response = staccatoService.createStaccatoShareLink(STACCATO_ID);
        String token = response.shareLink().split("token=")[1];

        // when & then
        assertThatCode(() -> shareTokenProvider.validateToken(token))
                .doesNotThrowAnyException();
    }
}
