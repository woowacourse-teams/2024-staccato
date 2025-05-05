package com.staccato.fixture.category;

import java.time.LocalDate;

import com.staccato.category.domain.Color;
import com.staccato.category.service.dto.request.CategoryRequestV3;

public class CategoryRequestV3Fixtures {

    public static CategoryRequestV3Builder defaultCategoryRequestV3() {
        return new CategoryRequestV3Builder()
                .withCategoryThumbnailUrl("https://example.com/categoryThumbnailUrl.jpg")
                .withCategoryTitle("categoryTitle")
                .withDescription("categoryDescription")
                .withColor(Color.PINK.getName())
                .withTerm(LocalDate.of(2024, 1, 1),
                        LocalDate.of(2024, 12, 31))
                .withIsShared(false);
    }

    public static class CategoryRequestV3Builder {
        private String categoryThumbnailUrl;
        private String categoryTitle;
        private String description;
        private String color;
        private LocalDate startAt;
        private LocalDate endAt;
        private Boolean isShared;

        public CategoryRequestV3Builder withCategoryThumbnailUrl(String categoryThumbnailUrl) {
            this.categoryThumbnailUrl = categoryThumbnailUrl;
            return this;
        }

        public CategoryRequestV3Builder withCategoryTitle(String categoryTitle) {
            this.categoryTitle = categoryTitle;
            return this;
        }

        public CategoryRequestV3Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public CategoryRequestV3Builder withTerm(LocalDate startAt, LocalDate endAt) {
            this.startAt = startAt;
            this.endAt = endAt;
            return this;
        }

        public CategoryRequestV3Builder withColor(String color) {
            this.color = color;
            return this;
        }

        public CategoryRequestV3Builder withIsShared(Boolean isShared) {
            this.isShared = isShared;
            return this;
        }

        public CategoryRequestV3 build() {
            return new CategoryRequestV3(categoryThumbnailUrl, categoryTitle, description, color, startAt, endAt, isShared);
        }
    }
}
