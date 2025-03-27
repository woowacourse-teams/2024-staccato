package com.staccato.staccato.domain;

import java.util.Arrays;

import com.staccato.exception.StaccatoException;

public enum Feeling {
    HAPPY("happy"),
    ANGRY("angry"),
    SAD("sad"),
    SCARED("scared"),
    EXCITED("excited"),
    NOTHING("nothing");

    private final String feeling;

    Feeling(String feeling) {
        this.feeling = feeling;
    }

    public static Feeling match(String value) {
        return Arrays.stream(values())
                .filter(mood -> mood.getValue().equals(value))
                .findFirst()
                .orElseThrow(() -> new StaccatoException("요청하신 기분 표현을 찾을 수 없어요."));
    }

    public String getValue() {
        return feeling;
    }
}
