package com.staccato.fixture.category;

import java.time.LocalDate;

import com.staccato.category.service.dto.request.CategoryRequest;

public class CategoryRequestFixtures {

    public static CategoryRequestBuilder defaultCategoryRequest() {
        return new CategoryRequestBuilder()
                .withCategoryThumbnailUrl("https://example.com/categories/geumohrm.jpg")
                .withCategoryTitle("2023 여름 휴가")
                .withDescription("친구들과 함께한 여름 휴가 추억")
                .withTerm(LocalDate.of(2023, 7, 1),
                        LocalDate.of(2024, 7, 10));
    }

    public static class CategoryRequestBuilder {
        String categoryThumbnailUrl;
        String categoryTitle;
        String description;
        LocalDate startAt;
        LocalDate endAt;

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

        public CategoryRequest build() {
            return new CategoryRequest(categoryThumbnailUrl, categoryTitle, description, startAt, endAt);
        }
    }
}
