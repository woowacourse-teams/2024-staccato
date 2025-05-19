package com.staccato.staccato.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import com.staccato.exception.StaccatoException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TitleTest {
    @DisplayName("정상적인 제목은 생성에 성공한다.")
    @Test
    void createValidTitle() {
        // given
        String value = "title";

        // when
        Title title = new Title(value);

        // then
        assertThat(title.getTitle()).isEqualTo(value);
    }

    @DisplayName("제목 앞,뒤 공백은 제거된다.")
    @Test
    void createTitleAfterTrim() {
        // given
        String value = " 스타카토 ";
        String trimmedValue = "스타카토";

        // when
        Title title = new Title(value);

        // then
        assertThat(title.getTitle()).isEqualTo(trimmedValue);
    }

    @DisplayName("제목이 1자 미만, 30자 초과이면 예외가 발생한다.")
    @ParameterizedTest
    @ValueSource(ints = {0, 31})
    void cannotCreateTitleIfIsTooLong(int count) {
        // given
        String value = "가".repeat(count);

        // when & then
        assertThatThrownBy(() -> new Title(value))
                .isInstanceOf(StaccatoException.class)
                .hasMessageContaining("스타카토 제목은 공백 포함 30자 이하로 설정해주세요.");
    }

    @DisplayName("제목이 1자 이상, 30자 이하이면 생성할 수 있다.")
    @ParameterizedTest
    @ValueSource(ints = {1, 30})
    void createTitleInRange(int count) {
        // given
        String value = "가".repeat(count);

        // when & then
        assertThatNoException().isThrownBy(() -> new Title(value));
    }
}
