package com.staccato.fixture.category;

import com.staccato.category.domain.Category;
import com.staccato.category.service.dto.response.CategoryNameResponses;
import java.util.Arrays;
import java.util.List;

public class CategoryNameResponsesFixture {
    public static CategoryNameResponses create(Category... category) {
        List<Category> memories = Arrays.stream(category).toList();
        return CategoryNameResponses.from(memories);
    }
}
