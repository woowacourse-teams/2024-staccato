package com.staccato.fixture.category;

import java.time.LocalDate;

import com.staccato.category.domain.Color;
import com.staccato.category.service.dto.request.CategoryRequest;

public class CategoryRequestFixtures {

    public static CategoryRequestBuilder defaultCategoryRequest() {
        return new CategoryRequestBuilder()
                .withCategoryThumbnailUrl("https://example.com/categoryThumbnailUrl.jpg")
                .withCategoryTitle("categoryTitle")
                .withDescription("categoryDescription")
                .withColor(Color.PINK.getName())
                .withTerm(LocalDate.of(2024, 1, 1),
                        LocalDate.of(2024, 12, 31));
    }

    public static class CategoryRequestBuilder {
        private String categoryThumbnailUrl;
        private String categoryTitle;
        private String description;
        private String color;
        private LocalDate startAt;
        private LocalDate endAt;

        public CategoryRequestBuilder withCategoryThumbnailUrl(String categoryThumbnailUrl) {
            this.categoryThumbnailUrl = categoryThumbnailUrl;
            return this;
        }

        public CategoryRequestBuilder withCategoryTitle(String categoryTitle) {
            this.categoryTitle = categoryTitle;
            return this;
        }

        public CategoryRequestBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public CategoryRequestBuilder withTerm(LocalDate startAt, LocalDate endAt) {
            this.startAt = startAt;
            this.endAt = endAt;
            return this;
        }

        public CategoryRequestBuilder withColor(String color) {
            this.color = color;
            return this;
        }

        public CategoryRequest build() {
            return new CategoryRequest(categoryThumbnailUrl, categoryTitle, description, color, startAt, endAt);
        }
    }
}
