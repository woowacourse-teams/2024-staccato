package com.staccato.category.service;

import com.staccato.category.domain.Category;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CategorySort {
    UPDATED("UPDATED", categories ->
        categories.stream()
                    .sorted(Comparator.comparing(Category::getUpdatedAt).reversed())
                    .toList()
    ),
    NEWEST("NEWEST", categories ->
        categories.stream()
                    .sorted(Comparator.comparing(Category::getCreatedAt).reversed())
                    .toList()
    ),
    OLDEST("OLDEST", categories ->
            categories.stream()
                    .sorted(Comparator.comparing(Category::getCreatedAt))
                    .toList()
    );

    private final String name;
    private final Function<List<Category>, List<Category>> operation;

    public List<Category> apply(List<Category> memories) {
        return operation.apply(memories);
    }

    public static CategorySort findByName(String name) {
        return Stream.of(values())
                .filter(sort -> sort.isSame(name))
                .findFirst()
                .orElse(UPDATED);
    }

    private boolean isSame(String name) {
        return this.name.equalsIgnoreCase(name);
    }
}
