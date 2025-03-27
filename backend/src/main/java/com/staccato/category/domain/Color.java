package com.staccato.category.domain;

import java.util.Arrays;
import com.staccato.exception.StaccatoException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
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

    public static Color findByName(String color) {
        return Arrays.stream(values())
                .filter(value -> value.name.equalsIgnoreCase(color))
                .findFirst()
                .orElseThrow(() -> new StaccatoException("유효하지 않은 색깔입니다."));
    }
}
