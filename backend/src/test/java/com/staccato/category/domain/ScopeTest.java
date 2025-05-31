package com.staccato.category.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.staccato.exception.StaccatoException;

public class ScopeTest {

    @DisplayName("문자열이 지정된 scope 값과 대응하면 Scope를 생성한다.")
    @ParameterizedTest
    @ValueSource(strings = {"all", "private", "ALL", "PRIVATE"})
    void canCreate(String validScope) {
        assertDoesNotThrow(() -> Scope.from(validScope));
    }

    @DisplayName("문자열이 지정된 scope 값과 대응하지 않으면 예외를 발생한다.")
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "public", "scope"})
    void cannotCreate(String invalidScope) {
        assertThatThrownBy(() -> Scope.from(invalidScope))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("올바르지 않은 scope 값입니다.");
    }
}
