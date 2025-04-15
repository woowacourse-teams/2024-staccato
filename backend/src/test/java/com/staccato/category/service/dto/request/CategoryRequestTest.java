package com.staccato.category.service.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CategoryRequestTest {
    @DisplayName("CategoryRequest를 생성할 때 title에는 trim이 적용된다.")
    @Test
    void trimTitle() {
        // given
        String categoryTitle = " title ";
        String expectedTitle = "title";

        // when
        CategoryRequest categoryRequest = new CategoryRequest(
                "thumbnail",
                categoryTitle,
                "description",
                "PINK",
                LocalDate.of(2024, 8, 22),
                LocalDate.of(2024, 8, 22)
        );

        // then
        assertThat(categoryRequest.categoryTitle()).isEqualTo(expectedTitle);
    }
}
