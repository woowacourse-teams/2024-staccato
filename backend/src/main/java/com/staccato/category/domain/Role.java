package com.staccato.category.domain;

import java.util.Objects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Role {

    HOST("host"),
    GUEST("guest");

    private final String role;

    boolean isGuest() {
        return Objects.equals(this, Role.GUEST);
    }
}
