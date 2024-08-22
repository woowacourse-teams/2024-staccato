package com.staccato.memory.service.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MemoryRequestTest {
    @DisplayName("MemoryRequest를 생성할 때 title에는 trim이 적용된다.")
    @Test
    void trimTitle() {
        // given
        String memoryTitle = " title ";
        String expectedTitle = "title";

        // when
        MemoryRequest memoryRequest = new MemoryRequest(
                "thumbnail",
                memoryTitle,
                "description",
                LocalDate.of(2024, 8, 22),
                LocalDate.of(2024, 8, 22)
        );

        // then
        assertThat(memoryRequest.memoryTitle()).isEqualTo(expectedTitle);
    }
}
