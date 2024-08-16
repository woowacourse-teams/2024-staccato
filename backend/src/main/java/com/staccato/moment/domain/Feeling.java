package com.staccato.moment.domain;

import java.util.Arrays;

import com.staccato.exception.StaccatoException;

public enum Feeling {
    HAPPY("happy"),
    ANGRY("angry"),
    SAD("sad"),
    SCARED("scared"),
    EXCITED("excited");

    private final String mood;

    Feeling(String mood) {
        this.mood = mood;
    }

    public static Feeling match(String value) {
        return Arrays.stream(values())
                .filter(mood -> mood.getMood().equals(value))
                .findFirst()
                .orElseThrow(() -> new StaccatoException("요청하신 기분 표현을 찾을 수 없어요."));
    }

    public String getMood() {
        return mood;
    }
}
