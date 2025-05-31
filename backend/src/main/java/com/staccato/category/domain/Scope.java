package com.staccato.category.domain;

import java.util.Arrays;

import com.staccato.exception.StaccatoException;

public enum Scope {
    ALL,
    PRIVATE;

    public static Scope from(String input) {
        return Arrays.stream(Scope.values())
                .filter(scope -> scope.name().equalsIgnoreCase(input.trim()))
                .findFirst()
                .orElseThrow(() -> new StaccatoException("올바르지 않은 scope 값입니다."));
    }

    public boolean isAll() {
        return this == ALL;
    }

    public boolean isPrivate() {
        return this == PRIVATE;
    }
}
