package com.staccato.category.domain;

import java.util.Arrays;
import com.staccato.exception.StaccatoException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Color {

    RED("red"),
    ORANGE("orange"),
    YELLOW("yellow"),
    GREEN("green"),
    BLUE("blue"),
    INDIGO("indigo"),
    PURPLE("purple"),
    PINK("pink"),
    GRAY("gray");

    private final String name;

    public static Color findByName(String color) {
        return Arrays.stream(values())
                .filter(value -> value.name.equalsIgnoreCase(color))
                .findFirst()
                .orElseThrow(() -> new StaccatoException("유효하지 않은 색깔입니다."));
    }
}
