package com.staccato.fixture.category;

import com.staccato.category.service.dto.request.CategoryRequest;
import java.time.LocalDate;

public class CategoryRequestFixture {
    public static CategoryRequest create(LocalDate startAt, LocalDate endAt) {
        return new CategoryRequest(
                "https://example.com/categories/geumohrm.jpg",
                "2023 여름 휴가",
                "친구들과 함께한 여름 휴가 추억",
                startAt,
                endAt
        );
    }

    public static CategoryRequest create(LocalDate startAt, LocalDate endAt, String title) {
        return new CategoryRequest(
                "https://example.com/categories/geumohrm.jpg",
                title,
                "친구들과 함께한 여름 휴가 추억",
                startAt,
                endAt
        );
    }

    public static CategoryRequest create(String imageUrl, LocalDate startAt, LocalDate endAt) {
        return new CategoryRequest(
                imageUrl,
                "2023 여름 휴가",
                "친구들과 함께한 여름 휴가 추억",
                startAt,
                endAt
        );
    }
}
