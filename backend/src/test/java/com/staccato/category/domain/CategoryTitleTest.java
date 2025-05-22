package com.staccato.category.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import com.staccato.exception.StaccatoException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CategoryTitleTest {
    @DisplayName("정상적인 제목은 생성에 성공한다.")
    @Test
    void createValidTitle() {
        // given
        String value = "title";

        // when
        CategoryTitle title = new CategoryTitle(value);

        // then
        assertThat(title.getTitle()).isEqualTo(value);
    }

    @DisplayName("제목 앞,뒤 공백은 제거된다.")
    @Test
    void createTitleAfterTrim() {
        // given
        String value = " 여행의 추억 ";
        String trimmedValue = "여행의 추억";

        // when
        CategoryTitle title = new CategoryTitle(value);

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
        assertThatThrownBy(() -> new CategoryTitle(value))
                .isInstanceOf(StaccatoException.class)
                .hasMessageContaining("제목은 공백 포함 30자 이하로 설정해주세요.");
    }

    @DisplayName("제목이 1자 이상, 30자 이하이면 생성할 수 있다.")
    @ParameterizedTest
    @ValueSource(ints = {1, 30})
    void createTitleInRange(int count) {
        // given
        String value = "가".repeat(count);

        // when & then
        assertThatNoException().isThrownBy(() -> new CategoryTitle(value));
    }

    @DisplayName("isSame 메서드는 제목이 동일한 경우 true를 반환한다.")
    @Test
    void isSameWhenTitleIsEqual() {
        // given
        CategoryTitle title1 = new CategoryTitle("스터디");
        CategoryTitle title2 = new CategoryTitle("스터디");

        // when & then
        assertThat(title1.isSame(title2)).isTrue();
    }

    @DisplayName("isSame 메서드는 제목이 다른 경우 false를 반환한다.")
    @Test
    void isNotSameWhenTitleIsDifferent() {
        // given
        CategoryTitle title1 = new CategoryTitle("스터디");
        CategoryTitle title2 = new CategoryTitle("여행");

        // when & then
        assertThat(title1.isSame(title2)).isFalse();
    }
}
