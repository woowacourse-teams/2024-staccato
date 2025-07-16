package com.staccato.category.service;

import java.time.LocalDateTime;
import java.util.Base64;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import com.staccato.exception.StaccatoException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StaccatoCursorTest {
    @DisplayName("정상적인 커서는 encode 후 decode 시 동일한 값으로 복원된다")
    @Test
    void encodeThenDecode() {
        // given
        StaccatoCursor original = new StaccatoCursor(
                42L,
                LocalDateTime.of(2025, 7, 12, 0, 0, 0)
        );

        // when
        String encoded = original.encode();
        StaccatoCursor decoded = StaccatoCursor.fromEncoded(encoded);

        // then
        assertThat(decoded).isEqualTo(original);
    }

    @DisplayName("Base64 인코딩이 아닌 평문 커서는 decode 시 예외를 던진다")
    @Test
    void cannotDecodePlainText() {
        // given
        String plainText = "42|2025-07-12T23:45:00|2025-07-12T23:00:00";

        // when & then
        assertThatThrownBy(() -> StaccatoCursor.fromEncoded(plainText))
                .isInstanceOf(StaccatoException.class)
                .hasMessageContaining("주어진 커서 포멧(id|visitedAt|createdAt)이 올바르지 않아요: ")
                .hasMessageContaining(plainText);
    }

    @DisplayName("필드 수가 3개가 아닌 경우 decode 시 예외를 던진다")
    @Test
    void cannotDecodeWrongFieldCount() {
        // given
        String encoded = encodeBase64("42|2025-07-12T23:45:00");

        // when & then
        assertThatThrownBy(() -> StaccatoCursor.fromEncoded(encoded))
                .isInstanceOf(StaccatoException.class)
                .hasMessageContaining("주어진 커서 포멧(id|visitedAt|createdAt)이 올바르지 않아요: ")
                .hasMessageContaining(encoded);
    }

    @DisplayName("id 값이 숫자가 아닌 경우 decode 시 예외를 던진다")
    @Test
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

    @DisplayName("visitedAt 필드의 날짜 형식이 잘못된 경우 decode 시 예외를 던진다")
    @Test
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

    @DisplayName("createdAt 필드의 날짜 형식이 잘못된 경우 decode 시 예외를 던진다")
    @Test
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

    @DisplayName("커서 값이 없거나 공백이면 empty()를 반환한다")
    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = {" "})
    void nullOrBlankCursorReturnsEmpty(String cursor) {
        // when
        StaccatoCursor result = StaccatoCursor.fromEncoded(cursor);

        // then
        assertThat(result).isEqualTo(StaccatoCursor.empty());
    }

    @DisplayName("EMPTY 커서는 isEmpty()가 true를 반환한다")
    @Test
    void isEmptyTrueWhenEmptyCursor() {
        // given
        StaccatoCursor emptyCursor = StaccatoCursor.empty();

        // when & then
        assertThat(emptyCursor.isEmpty()).isTrue();
    }

    @DisplayName("일반 커서는 isEmpty()가 false를 반환한다")
    @Test
    void isEmptyFalseWhenNotEmpty() {
        // given
        StaccatoCursor cursor = new StaccatoCursor(
                1L,
                LocalDateTime.of(2025, 7, 15, 12, 0)
        );

        // when & then
        assertThat(cursor.isEmpty()).isFalse();
    }

    @DisplayName("빈 커서는 encode() 시 null을 반환한다")
    @Test
    void encodeEmptyCursorReturnsNull() {
        // given
        StaccatoCursor cursor = StaccatoCursor.empty();

        // when
        String encoded = cursor.encode();

        // then
        assertThat(encoded).isNull();
    }

    private String encodeBase64(String raw) {
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(raw.getBytes());
    }
}
