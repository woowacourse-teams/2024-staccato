package com.staccato.category.domain;

public enum Scope {
    ALL,
    PRIVATE;

    public boolean isAll() {
        return this == ALL;
    }

    public boolean isPrivate() {
        return this == PRIVATE;
    }
}
