package com.staccato.exception;

public class ConflictException extends RuntimeException {
    public ConflictException() {
        super("요청하신 작업을 처리할 권한이 없습니다.");
    }

    public ConflictException(final String message) {
        super(message);
    }

    public ConflictException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ConflictException(final Throwable cause) {
        super(cause);
    }
}
