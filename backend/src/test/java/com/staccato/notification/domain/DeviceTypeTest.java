package com.staccato.notification.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.staccato.exception.StaccatoException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DeviceTypeTest {

    @DisplayName("적합한 디바이스 타입이 존재하지 않는다면 예외가 발생한다.")
    @Test
    void findByName() {
        assertThatThrownBy(() -> DeviceType.findByName("an"))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("유효하지 않은 디바이스 타입입니다.");
    }
}
