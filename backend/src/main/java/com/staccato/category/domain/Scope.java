package com.staccato.category.domain;

import java.util.Arrays;

public enum Scope {
    ALL,
    PRIVATE;

    public static Scope from(String input) {
        return Arrays.stream(Scope.values())
                .filter(scope -> scope.name().equalsIgnoreCase(input.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("일치하는 Scope 값이 없습니다."));
    }

    public boolean isAll() {
        return this == ALL;
    }

    public boolean isPrivate() {
        return this == PRIVATE;
    }
}
