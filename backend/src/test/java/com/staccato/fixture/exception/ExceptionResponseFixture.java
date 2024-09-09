package com.staccato.fixture.exception;

import org.springframework.http.HttpStatus;

public class ExceptionResponseFixture {
    public static String create(HttpStatus status, String message) {
        return "{"
                + "\"status\": \"" + status.toString() + "\","
                + "\"message\": \"" + message + "\""
                + "}";
    }
}
