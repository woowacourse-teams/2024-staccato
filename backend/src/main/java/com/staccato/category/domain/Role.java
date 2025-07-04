package com.staccato.category.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Role {

    HOST("host"),
    GUEST("guest");

    private final String role;

    boolean isGuest() {
        return this == Role.GUEST;
    }

    public boolean isHost() {
        return this == Role.HOST;
    }
}
