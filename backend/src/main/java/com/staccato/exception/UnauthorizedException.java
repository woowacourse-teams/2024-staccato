package com.staccato.exception;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException() {
        super("인증되지 않은 사용자입니다.");
    }

    public UnauthorizedException(final String message) {
        super(message);
    }

    public UnauthorizedException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public UnauthorizedException(final Throwable cause) {
        super(cause);
    }
}
