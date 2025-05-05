package com.staccato.fixture.category;

import java.time.LocalDate;

import com.staccato.category.domain.Color;
import com.staccato.category.service.dto.request.CategoryCreateRequest;

public class CategoryCreateRequestFixtures {

    public static CategoryCreateRequestBuilder defaultCategoryCreateRequest() {
        return new CategoryCreateRequestBuilder()
                .withCategoryThumbnailUrl("https://example.com/categoryThumbnailUrl.jpg")
                .withCategoryTitle("categoryTitle")
                .withDescription("categoryDescription")
                .withColor(Color.PINK.getName())
                .withTerm(LocalDate.of(2024, 1, 1),
                        LocalDate.of(2024, 12, 31))
                .withIsShared(false);
    }

    public static class CategoryCreateRequestBuilder {
        private String categoryThumbnailUrl;
        private String categoryTitle;
        private String description;
        private String color;
        private LocalDate startAt;
        private LocalDate endAt;
        private Boolean isShared;

        public CategoryCreateRequestBuilder withCategoryThumbnailUrl(String categoryThumbnailUrl) {
            this.categoryThumbnailUrl = categoryThumbnailUrl;
            return this;
        }

        public CategoryCreateRequestBuilder withCategoryTitle(String categoryTitle) {
            this.categoryTitle = categoryTitle;
            return this;
        }

        public CategoryCreateRequestBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public CategoryCreateRequestBuilder withTerm(LocalDate startAt, LocalDate endAt) {
            this.startAt = startAt;
            this.endAt = endAt;
            return this;
        }

        public CategoryCreateRequestBuilder withColor(String color) {
            this.color = color;
            return this;
        }

        public CategoryCreateRequestBuilder withIsShared(Boolean isShared) {
            this.isShared = isShared;
            return this;
        }

        public CategoryCreateRequest build() {
            return new CategoryCreateRequest(categoryThumbnailUrl, categoryTitle, description, color, startAt, endAt, isShared);
        }
    }
}
