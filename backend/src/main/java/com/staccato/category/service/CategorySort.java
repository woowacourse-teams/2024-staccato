package com.staccato.category.service;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import com.staccato.category.domain.Category;
import com.staccato.category.domain.Term;
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
    NEWEST("NEWEST", categories -> Stream.concat(
            categories.stream()
                    .filter(Category::hasTerm)
                    .sorted(Comparator
                            .comparing(Category::getTerm, Comparator.comparing(Term::getStartAt)).reversed()
                            .thenComparing(Category::getUpdatedAt, Comparator.reverseOrder())),
            sortCategoriesWithoutTerm(categories)
    ).toList()),
    OLDEST("OLDEST", categories -> Stream.concat(
            categories.stream()
                    .filter(Category::hasTerm)
                    .sorted(Comparator
                            .comparing(Category::getTerm, Comparator.comparing(Term::getStartAt))
                            .thenComparing(Category::getUpdatedAt, Comparator.naturalOrder())),
            sortCategoriesWithoutTerm(categories)
    ).toList()),
    ;

    private static Stream<Category> sortCategoriesWithoutTerm(List<Category> categories) {
        return categories.stream()
                .filter(category -> !category.hasTerm())
                .sorted(Comparator.comparing(Category::getUpdatedAt).reversed());
    }

    private final String name;
    private final Function<List<Category>, List<Category>> operation;

    public List<Category> apply(List<Category> categories) {
        return operation.apply(categories);
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
