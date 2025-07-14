package com.staccato.category.service;

import java.time.LocalDateTime;
import java.util.Base64;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.staccato.exception.StaccatoException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StaccatoCursorTest {
    @Test
    @DisplayName("정상적인 커서는 encode 후 decode 시 동일한 값으로 복원된다")
    void encodeThenDecode() {
        // given
        StaccatoCursor original = new StaccatoCursor(
                42L,
                LocalDateTime.of(2025, 7, 12, 0, 0, 0),
                LocalDateTime.of(2025, 7, 12, 0, 0, 0)
        );

        // when
        String encoded = original.encode();
        StaccatoCursor decoded = StaccatoCursor.fromEncoded(encoded);

        // then
        assertThat(decoded).isEqualTo(original);
    }

    @Test
    @DisplayName("Base64 인코딩이 아닌 평문 커서는 decode 시 예외를 던진다")
    void cannotDecodePlainText() {
        // given
        String plainText = "42|2025-07-12T23:45:00|2025-07-12T23:00:00";

        // when & then
        assertThatThrownBy(() -> StaccatoCursor.fromEncoded(plainText))
                .isInstanceOf(StaccatoException.class)
                .hasMessageContaining("주어진 커서 포멧(id|visitedAt|createdAt)이 올바르지 않아요: ")
                .hasMessageContaining(plainText);
    }

    @Test
    @DisplayName("필드 수가 3개가 아닌 경우 decode 시 예외를 던진다")
    void cannotDecodeWrongFieldCount() {
        // given
        String encoded = encodeBase64("42|2025-07-12T23:45:00");

        // when & then
        assertThatThrownBy(() -> StaccatoCursor.fromEncoded(encoded))
                .isInstanceOf(StaccatoException.class)
                .hasMessageContaining("주어진 커서 포멧(id|visitedAt|createdAt)이 올바르지 않아요: ")
                .hasMessageContaining(encoded);
    }

    @Test
    @DisplayName("id 값이 숫자가 아닌 경우 decode 시 예외를 던진다")
    void cannotDecodeInvalidId() {
        // given
        String raw = "abc|2025-07-12T23:45:00|2025-07-12T23:00:00";
        String encoded = encodeBase64(raw);

        // when & then
        assertThatThrownBy(() -> StaccatoCursor.fromEncoded(encoded))
                .isInstanceOf(StaccatoException.class)
                .hasMessageContaining("주어진 커서 포멧(id|visitedAt|createdAt)이 올바르지 않아요: ")
                .hasMessageContaining(encoded);
    }

    @Test
    @DisplayName("visitedAt 필드의 날짜 형식이 잘못된 경우 decode 시 예외를 던진다")
    void cannotDecodeInvalidVisitedAt() {
        // given
        String raw = "42|invalid-date|2025-07-12T23:00:00";
        String encoded = encodeBase64(raw);

        // when & then
        assertThatThrownBy(() -> StaccatoCursor.fromEncoded(encoded))
                .isInstanceOf(StaccatoException.class)
                .hasMessageContaining("주어진 커서 포멧(id|visitedAt|createdAt)이 올바르지 않아요: ")
                .hasMessageContaining(encoded);
    }

    @Test
    @DisplayName("createdAt 필드의 날짜 형식이 잘못된 경우 decode 시 예외를 던진다")
    void cannotDecodeInvalidCreatedAt() {
        // given
        String raw = "42|2025-07-12T23:45:00|invalid-created";
        String encoded = encodeBase64(raw);

        // when
        assertThatThrownBy(() -> StaccatoCursor.fromEncoded(encoded))
                .isInstanceOf(StaccatoException.class)
                .hasMessageContaining("주어진 커서 포멧(id|visitedAt|createdAt)이 올바르지 않아요: ")
                .hasMessageContaining(encoded);
    }

    private String encodeBase64(String raw) {
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(raw.getBytes());
    }
}
