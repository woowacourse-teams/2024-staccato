package com.staccato.category.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Color {

    RED("red", "F4828E"),
    ORANGE("orange", "FFAE8C"),
    YELLOW("yellow", "F8DB90"),
    GREEN("green", "C3E28D"),
    BLUE("blue", "A0C0F6"),
    INDIGO("indigo", "A6A6F6"),
    PURPLE("purple", "D0AAFE"),
    PINK("pink", "FAB0D2"),
    GRAY("gray", "D2D2D2");

    private final String name;
    private final String hexCode;
}
