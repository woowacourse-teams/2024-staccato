package com.staccato.exception;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException() {
        super("요청하신 작업을 처리할 권한이 없습니다.");
    }

    public ForbiddenException(final String message) {
        super(message);
    }

    public ForbiddenException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ForbiddenException(final Throwable cause) {
        super(cause);
    }
}
