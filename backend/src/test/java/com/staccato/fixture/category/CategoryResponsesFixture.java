package com.staccato.fixture.category;

import com.staccato.category.domain.Category;
import com.staccato.category.service.dto.response.CategoryResponse;
import com.staccato.category.service.dto.response.CategoryResponses;
import java.util.Arrays;
import java.util.List;

public class CategoryResponsesFixture {
    public static CategoryResponses create(Category... categories) {
        return new CategoryResponses(convertToCategoryResponses(Arrays.stream(categories).toList()));
    }

    private static List<CategoryResponse> convertToCategoryResponses(List<Category> category) {
        return category.stream().map(CategoryResponse::new).toList();
    }
}
