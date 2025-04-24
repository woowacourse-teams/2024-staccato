package com.staccato.category.domain;

import java.util.Arrays;
import com.staccato.exception.StaccatoException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Color {

    LIGHT_RED("light_red"),
    RED("red"),
    LIGHT_ORANGE("light_orange"),
    ORANGE("orange"),
    LIGHT_YELLOW("light_yellow"),
    YELLOW("yellow"),
    LIGHT_GREEN("light_green"),
    GREEN("green"),
    LIGHT_MINT("light_mint"),
    MINT("mint"),
    LIGHT_BLUE("light_blue"),
    BLUE("blue"),
    LIGHT_INDIGO("light_indigo"),
    INDIGO("indigo"),
    LIGHT_PURPLE("light_purple"),
    PURPLE("purple"),
    LIGHT_PINK("light_pink"),
    PINK("pink"),
    LIGHT_BROWN("light_brown"),
    BROWN("brown"),
    LIGHT_GRAY("light_gray"),
    GRAY("gray"),
    ;

    private final String name;

    public static Color findByName(String color) {
        return Arrays.stream(values())
                .filter(value -> value.name.equalsIgnoreCase(color))
                .findFirst()
                .orElseThrow(() -> new StaccatoException("유효하지 않은 색깔입니다."));
    }
}
