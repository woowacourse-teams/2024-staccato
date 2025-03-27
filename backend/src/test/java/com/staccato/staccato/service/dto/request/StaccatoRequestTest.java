package com.staccato.staccato.service.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StaccatoRequestTest {

    @DisplayName("StaccatoRequest를 생성할 때 staccatoTitle에는 trim이 적용된다.")
    @Test
    void trimPlaceName() {
        // given
        String staccatoTitle = " staccatoTitle ";
        String expectedPlaceName = "staccatoTitle";

        // when
        StaccatoRequest staccatoRequest = new StaccatoRequest(
            staccatoTitle,
            "placeName",
            "address",
            BigDecimal.ONE,
            BigDecimal.ONE,
            LocalDateTime.of(2024, 8, 22, 10, 0),
            1L,
            List.of()
        );

        // then
        assertThat(staccatoRequest.staccatoTitle()).isEqualTo(expectedPlaceName);
    }
}