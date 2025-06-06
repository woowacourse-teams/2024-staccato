package com.staccato.fixture.category;

import java.time.LocalDate;

import com.staccato.category.domain.Color;
import com.staccato.category.service.dto.request.CategoryRequestV2;

public class CategoryRequestV2Fixtures {

    public static CategoryRequestV2Builder defaultCategoryRequestV2() {
        return new CategoryRequestV2Builder()
                .withCategoryThumbnailUrl("https://example.com/categoryThumbnailUrl.jpg")
                .withCategoryTitle("categoryTitle")
                .withDescription("categoryDescription")
                .withColor(Color.PINK.getName())
                .withTerm(LocalDate.of(2024, 1, 1),
                        LocalDate.of(2024, 12, 31));
    }

    public static class CategoryRequestV2Builder {
        private String categoryThumbnailUrl;
        private String categoryTitle;
        private String description;
        private String color;
        private LocalDate startAt;
        private LocalDate endAt;

        public CategoryRequestV2Builder withCategoryThumbnailUrl(String categoryThumbnailUrl) {
            this.categoryThumbnailUrl = categoryThumbnailUrl;
            return this;
        }

        public CategoryRequestV2Builder withCategoryTitle(String categoryTitle) {
            this.categoryTitle = categoryTitle;
            return this;
        }

        public CategoryRequestV2Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public CategoryRequestV2Builder withTerm(LocalDate startAt, LocalDate endAt) {
            this.startAt = startAt;
            this.endAt = endAt;
            return this;
        }

        public CategoryRequestV2Builder withColor(String color) {
            this.color = color;
            return this;
        }

        public CategoryRequestV2 build() {
            return new CategoryRequestV2(categoryThumbnailUrl, categoryTitle, description, color, startAt, endAt);
        }
    }
}
