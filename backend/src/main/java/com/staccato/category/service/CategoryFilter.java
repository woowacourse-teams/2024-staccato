package com.staccato.category.service;

import com.staccato.category.domain.Category;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CategoryFilter {
    TERM("term", categories ->
        categories.stream()
                    .filter(Category::hasTerm)
                    .collect(Collectors.toList())
    );

    private final String name;
    private final Function<List<Category>, List<Category>> operation;

    public List<Category> apply(List<Category> categories) {
        return operation.apply(categories);
    }

    public static List<CategoryFilter> findAllByName(List<String> filters) {
        return filters.stream()
                .map(name -> CategoryFilter.findByName(name.trim()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .distinct()
                .toList();
    }

    private static Optional<CategoryFilter> findByName(String name) {
        return Stream.of(values())
                .filter(value -> value.isSame(name))
                .findFirst();
    }

    private boolean isSame(String name) {
        return this.name.equalsIgnoreCase(name);
    }
}
