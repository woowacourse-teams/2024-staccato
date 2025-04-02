package com.staccato.category.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.staccato.exception.StaccatoException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class TermTest {
    @DisplayName("끝 날짜는 시작 날짜보다 앞설 수 없다.")
    @Test
    void validateDate() {
        assertThatThrownBy(() -> new Term(LocalDate.of(2024, 12, 31),
                LocalDate.of(2024, 12, 30)))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("끝 날짜가 시작 날짜보다 앞설 수 없어요.");
    }

    @DisplayName("특정 날짜가 카테고리 기간에 속하지 않으면 참을 반환한다.")
    @Test
    void isOutOfTerm() {
        // given
        Term term = new Term(LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 12, 31));

        // when & then
        assertThat(term.doesNotContain(LocalDateTime.of(2023, 6, 1, 0, 0))).isTrue();
    }

    @DisplayName("특정 날짜가 카테고리 기간에 속하면 거짓을 반환한다.")
    @Test
    void isInTerm() {
        // given
        Term term = new Term(LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 12, 31));

        // when & then
        assertThat(term.doesNotContain(LocalDateTime.of(2024, 6, 1, 0, 0))).isFalse();
    }

    @DisplayName("카테고리 기간이 없다면, 어떤 날짜든 거짓을 반환한다.")
    @Test
    void isNoTerm() {
        // given
        Term term = new Term(null, null);

        // when & then
        assertThat(term.doesNotContain(LocalDateTime.of(2024, 6, 1, 0, 0))).isFalse();
    }

    @DisplayName("끝 날짜는 있는데, 시작 날짜가 누락되면 예외를 발생한다.")
    @Test
    void cannotCreateTermByNoStartAt() {
        assertThatThrownBy(() -> new Term(null, LocalDate.of(2024, 12, 31)))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("카테고리의 시작 날짜와 끝 날짜는 함께 입력되거나, 함께 비워져 있어야 합니다.");
    }

    @DisplayName("시작 날짜는 있는데, 끝 날짜가 누락되면 예외를 발생한다.")
    @Test
    void cannotCreateTermByNoEndAt() {
        assertThatThrownBy(() -> new Term(LocalDate.of(2024, 1, 1), null))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("카테고리의 시작 날짜와 끝 날짜는 함께 입력되거나, 함께 비워져 있어야 합니다.");
    }

    @DisplayName("기간이 있으면 참을 반환한다.")
    @Test
    void isNotEmpty() {
        // given
        Term term = new Term(LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 12, 31));

        // when
        boolean result = term.isExist();

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("기간이 없으면 거짓을 반환한다.")
    @Test
    void isEmpty() {
        // given
        Term term = new Term(null, null);

        // when
        boolean result = term.isExist();

        // then
        assertThat(result).isFalse();
    }
}
