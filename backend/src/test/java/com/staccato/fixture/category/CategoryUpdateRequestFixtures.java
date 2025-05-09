package com.staccato.fixture.category;

import java.time.LocalDate;

import com.staccato.category.domain.Color;
import com.staccato.category.service.dto.request.CategoryUpdateRequest;

public class CategoryUpdateRequestFixtures {

    public static CategoryUpdateRequestBuilder defaultCategoryUpdateRequest() {
        return new CategoryUpdateRequestBuilder()
                .withCategoryThumbnailUrl("https://example.com/categoryThumbnailUrl.jpg")
                .withCategoryTitle("categoryTitle")
                .withDescription("categoryDescription")
                .withColor(Color.PINK.getName())
                .withTerm(LocalDate.of(2024, 1, 1),
                        LocalDate.of(2024, 12, 31));
    }

    public static class CategoryUpdateRequestBuilder {
        private String categoryThumbnailUrl;
        private String categoryTitle;
        private String description;
        private String color;
        private LocalDate startAt;
        private LocalDate endAt;

        public CategoryUpdateRequestBuilder withCategoryThumbnailUrl(String categoryThumbnailUrl) {
            this.categoryThumbnailUrl = categoryThumbnailUrl;
            return this;
        }

        public CategoryUpdateRequestBuilder withCategoryTitle(String categoryTitle) {
            this.categoryTitle = categoryTitle;
            return this;
        }

        public CategoryUpdateRequestBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public CategoryUpdateRequestBuilder withTerm(LocalDate startAt, LocalDate endAt) {
            this.startAt = startAt;
            this.endAt = endAt;
            return this;
        }

        public CategoryUpdateRequestBuilder withColor(String color) {
            this.color = color;
            return this;
        }

        public CategoryUpdateRequest build() {
            return new CategoryUpdateRequest(categoryThumbnailUrl, categoryTitle, description, color, startAt, endAt);
        }
    }
}
